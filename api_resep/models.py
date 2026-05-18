from django.db import models

class Resep(models.Model):
    judul = models.CharField(max_length=255)
    bahan = models.TextField()
    langkah_pembuatan = models.TextField()
    gambar = models.ImageField(upload_to='resep_images/', blank=True, null=True)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return self.judul