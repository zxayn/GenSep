from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from django.contrib.auth.models import User
from .models import Resep, Riwayat
import json

# ==========================================
# 1. FITUR SEARCH (GET) - YANG SUDAH BERES SEMALAM
# ==========================================
def search_resep(request):
    query = request.GET.get('search', '')
    if query:
        hasil = Resep.objects.filter(judul__icontains=query)
    else:
        hasil = Resep.objects.all()
        
    data = []
    for resep in hasil:
        data.append({
            'id': resep.id,
            'judul': resep.judul,
            'bahan': resep.bahan,
            'langkah': resep.langkah
        })
    return JsonResponse(data, safe=False)


# ==========================================
# 2. FITUR RIWAYAT (GET, POST, DELETE) - TUGAS BARU
# ==========================================
@csrf_exempt
def kelola_riwayat(request):
    # Karena kita belum pakai sistem login token yang rumit, 
    # kita tembak dulu menggunakan user pertama (admin) yang kamu buat semalam.
    user_default = User.objects.first()
    
    if not user_default:
        return JsonResponse({'error': 'Belum ada user/admin di database. Silakan buat superuser dulu.'}, status=400)

    # --------------------------------------
    # A. METODE GET: Mengambil semua riwayat resep yang pernah dilihat
    # --------------------------------------
    if request.method == 'GET':
        riwayat_queryset = Riwayat.objects.filter(user=user_default).order_by('-tanggal_dilihat')
        data_riwayat = []
        for r in riwayat_queryset:
            data_riwayat.append({
                'id_riwayat': r.id,
                'resep_id': r.resep.id,
                'judul_resep': r.resep.judul,
                'tanggal_dilihat': r.tanggal_dilihat.strftime('%Y-%m-%d %H:%M:%S')
            })
        return JsonResponse(data_riwayat, safe=False)

    # --------------------------------------
    # B. METODE POST: Menambahkan resep ke dalam riwayat saat user klik resep
    # --------------------------------------
    elif request.method == 'POST':
        try:
            body = json.loads(request.body)
            resep_id = body.get('resep_id')
            
            # Cek apakah resepnya beneran ada di database
            resep = Resep.objects.get(id=resep_id)
            
            # Simpan ke tabel Riwayat
            riwayat_baru = Riwayat.objects.create(user=user_default, resep=resep)
            
            return JsonResponse({
                'message': 'Riwayat berhasil ditambahkan!',
                'id_riwayat': riwayat_baru.id
            }, status=201)
            
        except Resep.DoesNotExist:
            return JsonResponse({'error': 'Resep tidak ditemukan'}, status=404)
        except Exception as e:
            return JsonResponse({'error': str(e)}, status=400)

    # --------------------------------------
    # C. METODE DELETE: Menghapus riwayat tertentu (misal user klik hapus riwayat)
    # --------------------------------------
    elif request.method == 'DELETE':
        try:
            body = json.loads(request.body)
            riwayat_id = body.get('riwayat_id')
            
            # Cari riwayatnya lalu hapus
            riwayat_data = Riwayat.objects.get(id=riwayat_id, user=user_default)
            riwayat_data.delete()
            
            return JsonResponse({'message': 'Riwayat berhasil dihapus!'}, status=200)
            
        except Riwayat.DoesNotExist:
            return JsonResponse({'error': 'Riwayat tidak ditemukan'}, status=404)
        except Exception as e:
            return JsonResponse({'error': str(e)}, status=400)

    # Jika ada yang mengakses selain GET, POST, DELETE
    return JsonResponse({'error': 'Metode tidak diizinkan'}, status=405)