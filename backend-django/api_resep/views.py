from django.http import JsonResponse
from django.db.models import Q
from .models import Resep

def search_resep(request):
    # Mengambil parameter 'q' dari URL, contoh: /api/search/?q=ayam
    query = request.GET.get('q', '')
    
    if query:
        # Mencari resep yang judul atau bahannya mengandung kata kunci (case-insensitive)
        hasil_pencarian = Resep.objects.filter(
            Q(judul__icontains=query) | Q(bahan__icontains=query)
        )
    else:
        hasil_pencarian = Resep.objects.all()

    # Mengubah hasil data dari database menjadi format JSON agar bisa dibaca Android
    data = []
    for resep in hasil_pencarian:
        data.append({
            'id': resep.id,
            'judul': resep.judul,
            'bahan': resep.bahan,
            'langkah_pembuatan': resep.langkah_pembuatan,
            'gambar': resep.gambar.url if resep.gambar else None,
        })

    return JsonResponse({'status': 'success', 'data': data}, safe=False)