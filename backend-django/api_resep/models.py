from django.db import models
from django.contrib.auth.models import User

class Resep(models.Model):
    # Tambahan: Relasi ke User agar bisa jadi "Riwayat"
    pembuat = models.ForeignKey(User, on_delete=models.CASCADE, null=True, blank=True)
    
    judul = models.CharField(max_length=255)
    bahan = models.TextField()
    langkah_pembuatan = models.TextField()
    gambar = models.ImageField(upload_to='resep_images/', blank=True, null=True)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return self.judul

class Catatan(models.Model):
    # Tabel baru untuk Catatan Belanja
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    judul = models.CharField(max_length=255)
    bahan = models.TextField() # Akan disimpan dalam bentuk JSON string (List)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.judul} - {self.user.username}"