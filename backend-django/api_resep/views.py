import json
import uuid
import traceback # <-- Tambahan untuk melacak detail error
from django.http import JsonResponse
from django.db.models import Q
from django.views.decorators.csrf import csrf_exempt
from django.contrib.auth.models import User
from django.contrib.auth import authenticate
from ultralytics import YOLO
from PIL import Image
import google.generativeai as genai
from .models import Catatan, Resep 

# ==========================================
# 1. KONFIGURASI MODEL AI (YOLO & GEMINI)
# ==========================================
try:
    yolo_model = YOLO('best.pt')
except Exception as e:
    print(f"Error loading YOLO model: {e}")
    yolo_model = None

# GANTI DENGAN API KEY GEMINI ANDA
genai.configure(api_key="API_KEY_GEMINI_ANDA_DISINI")
gemini_model = genai.GenerativeModel('gemini-3.5-flash')

# ==========================================
# 2. FITUR AUTENTIKASI (LOGIN & REGISTER)
# ==========================================
@csrf_exempt
def register_user(request):
    if request.method == 'POST':
        try:
            data = json.loads(request.body)
            username = data.get('username')
            email = data.get('email')
            password = data.get('password')
            
            # Cek apakah username sudah ada
            if User.objects.filter(username=username).exists():
                return JsonResponse({'token': None, 'username': None, 'error': 'Username sudah digunakan!'})

            user = User.objects.create_user(username=username, email=email, password=password)
            token = uuid.uuid4().hex # Buat token sederhana
            
            return JsonResponse({'token': token, 'username': user.username, 'error': None})
        except Exception as e:
            return JsonResponse({'token': None, 'username': None, 'error': str(e)})

@csrf_exempt
def login_user(request):
    if request.method == 'POST':
        try:
            data = json.loads(request.body)
            username = data.get('username')
            password = data.get('password')
            
            user = authenticate(username=username, password=password)
            if user is not None:
                token = uuid.uuid4().hex
                return JsonResponse({'token': token, 'username': user.username, 'error': None})
            else:
                return JsonResponse({'token': None, 'username': None, 'error': 'Username atau password salah!'})
        except Exception as e:
            return JsonResponse({'token': None, 'username': None, 'error': str(e)})


# ==========================================
# 3. FITUR GENERATE RESEP DARI TEKS
# ==========================================
def generate_from_text(request):
    query = request.GET.get('q', '')
    username = request.GET.get('username')
    
    # [BARU] Pengecekan parameter dan user yang lebih ketat
    if not query:
        return JsonResponse({'status': 'error', 'message': 'Parameter pencarian kosong'}, status=400)
        
    if not username:
        return JsonResponse({'status': 'error', 'message': 'Username tidak dikirimkan dari Android'}, status=400)
        
    user = User.objects.filter(username=username).first()
    if not user:
        return JsonResponse({'status': 'error', 'message': f'User "{username}" tidak ditemukan di database'}, status=404)
        
    try:
        prompt = f"""
        Buatkan resep masakan detail berdasarkan kata kunci: {query}.
        Wajib kembalikan HANYA dalam format JSON persis seperti ini:
        {{
          "title": "Nama Masakan",
          "description": "Deskripsi singkat",
          "cookingTime": "X Menit",
          "difficulty": "Mudah",
          "ingredients": ["bahan 1", "bahan 2"],
          "steps": ["langkah 1", "langkah 2"]
        }}
        """
        response = gemini_model.generate_content(prompt)
        hasil_teks = response.text.strip()
        
        if hasil_teks.startswith("```json"):
            hasil_teks = hasil_teks.replace("```json", "").replace("```", "").strip()
            
        resep_json = json.loads(hasil_teks)
        
        # Simpan ke Database
        Resep.objects.create(
            pembuat=user,
            judul=resep_json.get('title'),
            bahan=json.dumps(resep_json.get('ingredients')),
            langkah_pembuatan=json.dumps(resep_json.get('steps'))
        )
        
        return JsonResponse({
            'status': 'success',
            'message': 'Resep berhasil dibuat',
            'detectedingredients': None,
            'data': resep_json
        })
    except Exception as e:
        # [BARU] Sistem Pelacak Error di Terminal
        print("\n" + "="*50)
        print("🚨 ERROR DI FITUR GENERATE TEXT 🚨")
        print("Pesan Error :", str(e))
        print("Detail      :")
        traceback.print_exc()
        print("="*50 + "\n")
        
        return JsonResponse({'status': 'error', 'message': str(e)}, status=500)


# ==========================================
# 4. FITUR DETEKSI YOLO & GENERATE
# ==========================================
@csrf_exempt
def detect_and_generate(request):
    if request.method == 'POST' and request.FILES.get('image'):
        try:
            image_file = request.FILES['image']
            img = Image.open(image_file)
            
            # Deteksi YOLO
            if yolo_model:
                results = yolo_model(img)
                detected_classes = set()
                for r in results:
                    for box in r.boxes:
                        class_id = int(box.cls[0])
                        class_name = yolo_model.names[class_id]
                        detected_classes.add(class_name)
                
                detected_list = list(detected_classes)
            else:
                detected_list = ["Bahan tidak diketahui"]

            bahan_str = ", ".join(detected_list)
            
            prompt = f"""
            Saya memiliki bahan-bahan masakan ini: {bahan_str}.
            Tolong buatkan satu resep lezat.
            Wajib kembalikan HANYA dalam format JSON persis seperti ini:
            {{
              "title": "Nama Masakan",
              "description": "Deskripsi",
              "cookingTime": "X Menit",
              "difficulty": "Sedang",
              "ingredients": ["{bahan_str}", "bumbu rahasia"],
              "steps": ["langkah 1", "langkah 2"]
            }}
            """
            
            response = gemini_model.generate_content(prompt)
            hasil_teks = response.text.strip()
            if hasil_teks.startswith("```json"):
                hasil_teks = hasil_teks.replace("```json", "").replace("```", "").strip()
                
            resep_json = json.loads(hasil_teks)
            
            # Ambil user jika ada
            username = request.POST.get('username') # Karena multipart/form-data
            user = User.objects.filter(username=username).first() if username else None

            # Simpan Foto dan Resep ke Database
            Resep.objects.create(
                pembuat=user,
                judul=resep_json.get('title'),
                bahan=json.dumps(resep_json.get('ingredients')),
                langkah_pembuatan=json.dumps(resep_json.get('steps')),
                gambar=image_file 
            )

            return JsonResponse({
                'status': 'success',
                'message': 'Bahan dideteksi dan resep dibuat',
                'detectedingredients': detected_list,
                'data': resep_json
            })
            
        except Exception as e:
            # [BARU] Sistem Pelacak Error di Terminal
            print("\n" + "="*50)
            print("🚨 ERROR DI FITUR DETEKSI KAMERA 🚨")
            print("Pesan Error :", str(e))
            print("Detail      :")
            traceback.print_exc()
            print("="*50 + "\n")
            return JsonResponse({'status': 'error', 'message': str(e)}, status=500)
             
    return JsonResponse({'status': 'error', 'message': 'Hanya menerima POST dengan gambar'}, status=400)


# ==========================================
# 5. FITUR PENCARIAN TEKS DATABASE
# ==========================================
def search_resep(request):
    query = request.GET.get('q', '')
    if query:
        hasil = Resep.objects.filter(Q(judul__icontains=query) | Q(bahan__icontains=query))
    else:
        hasil = Resep.objects.all()

    data = []
    for r in hasil:
        try:
            bahan_list = json.loads(r.bahan) if r.bahan else []
        except:
            bahan_list = [r.bahan]
            
        data.append({
            'id': r.id,
            'title': r.judul,
            'ingredients': bahan_list
        })
    return JsonResponse({'data': data})

# ==========================================
# 6. FITUR RIWAYAT & CATATAN (CLOUD SYNC)
# ==========================================

@csrf_exempt
def get_riwayat(request):
    # Mengambil resep berdasarkan username
    username = request.GET.get('username')
    if not username:
        return JsonResponse({'error': 'Username diperlukan'}, status=400)
        
    hasil = Resep.objects.filter(pembuat__username=username).order_by('-created_at')
    data = []
    for r in hasil:
        try:
            bahan_list = json.loads(r.bahan) if r.bahan else []
        except:
            bahan_list = [r.bahan]
            
        data.append({
            'id': r.id,
            'title': r.judul,
            'ingredients': bahan_list
        })
    return JsonResponse({'data': data})

@csrf_exempt
def get_catatan(request):
    # Mengambil catatan milik user
    username = request.GET.get('username')
    if not username:
        return JsonResponse({'error': 'Username diperlukan'}, status=400)
        
    catatan = Catatan.objects.filter(user__username=username).order_by('-created_at')
    data = []
    for c in catatan:
        try:
            bahan_list = json.loads(c.bahan) if c.bahan else []
        except:
            bahan_list = [c.bahan]
            
        data.append({
            'id': c.id,
            'title': c.judul,
            'ingredients': bahan_list
        })
    return JsonResponse({'data': data})

@csrf_exempt
def tambah_catatan(request):
    # Menyimpan catatan baru dari Android ke Django
    if request.method == 'POST':
        try:
            data = json.loads(request.body)
            username = data.get('username')
            judul = data.get('title', 'Catatan Baru')
            bahan = data.get('ingredients', [])
            
            user = User.objects.get(username=username)
            Catatan.objects.create(
                user=user,
                judul=judul,
                bahan=json.dumps(bahan)
            )
            return JsonResponse({'status': 'success', 'message': 'Catatan berhasil disimpan'})
        except User.DoesNotExist:
            return JsonResponse({'status': 'error', 'message': 'User tidak ditemukan'}, status=404)
        except Exception as e:
            return JsonResponse({'status': 'error', 'message': str(e)}, status=500)
            
    return JsonResponse({'status': 'error', 'message': 'Method tidak diizinkan'}, status=405)

# ==========================================
# 7. FITUR EDIT & HAPUS (UPDATE & DELETE)
# ==========================================

@csrf_exempt
def hapus_riwayat(request, resep_id):
    # Endpoint ini bisa menggunakan method POST atau DELETE dari Android
    if request.method in ['POST', 'DELETE']:
        try:
            resep = Resep.objects.get(id=resep_id)
            resep.delete()
            return JsonResponse({'status': 'success', 'message': 'Riwayat resep berhasil dihapus'})
        except Resep.DoesNotExist:
            return JsonResponse({'status': 'error', 'message': 'Resep tidak ditemukan'}, status=404)
    return JsonResponse({'status': 'error', 'message': 'Method tidak diizinkan'}, status=405)


@csrf_exempt
def edit_catatan(request, catatan_id):
    if request.method in ['POST', 'PUT']:
        try:
            data = json.loads(request.body)
            catatan = Catatan.objects.get(id=catatan_id)
            
            # Update data jika dikirim dari Android
            if 'title' in data:
                catatan.judul = data['title']
            if 'ingredients' in data:
                catatan.bahan = json.dumps(data['ingredients'])
                
            catatan.save()
            return JsonResponse({'status': 'success', 'message': 'Catatan berhasil diperbarui'})
        except Catatan.DoesNotExist:
            return JsonResponse({'status': 'error', 'message': 'Catatan tidak ditemukan'}, status=404)
        except Exception as e:
            return JsonResponse({'status': 'error', 'message': str(e)}, status=500)
    return JsonResponse({'status': 'error', 'message': 'Method tidak diizinkan'}, status=405)


@csrf_exempt
def hapus_catatan(request, catatan_id):
    if request.method in ['POST', 'DELETE']:
        try:
            catatan = Catatan.objects.get(id=catatan_id)
            catatan.delete()
            return JsonResponse({'status': 'success', 'message': 'Catatan berhasil dihapus'})
        except Catatan.DoesNotExist:
            return JsonResponse({'status': 'error', 'message': 'Catatan tidak ditemukan'}, status=404)
    return JsonResponse({'status': 'error', 'message': 'Method tidak diizinkan'}, status=405)

# ==========================================
# 8. FITUR DETEKSI GAMBAR SAJA (TANPA GENERATE RESEP)
# ==========================================
@csrf_exempt
def detect_only(request):
    if request.method == 'POST' and request.FILES.get('image'):
        try:
            image_file = request.FILES['image']
            img = Image.open(image_file)
            
            # Deteksi YOLO
            if yolo_model:
                results = yolo_model(img)
                detected_classes = set()
                for r in results:
                    for box in r.boxes:
                        class_id = int(box.cls[0])
                        class_name = yolo_model.names[class_id]
                        detected_classes.add(class_name)
                
                detected_list = list(detected_classes)
            else:
                detected_list = ["Bahan tidak diketahui"]

            return JsonResponse({
                'status': 'success',
                'message': 'Bahan berhasil dideteksi',
                'detectedingredients': detected_list
            })
            
        except Exception as e:
            print("\n" + "="*50)
            print("🚨 ERROR DI FITUR DETECT ONLY 🚨")
            print(str(e))
            print("="*50 + "\n")
            return JsonResponse({'status': 'error', 'message': str(e)}, status=500)
             
    return JsonResponse({'status': 'error', 'message': 'Hanya menerima POST dengan gambar'}, status=400)