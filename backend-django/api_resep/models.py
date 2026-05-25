from django.db import models
from django.contrib.auth.models import User

class Resep(models.Model):
    judul = models.CharField(max_length=255)
    bahan = models.TextField()
    langkah = models.TextField()

    def __str__(self):
        return self.judul

class Riwayat(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    resep = models.ForeignKey(Resep, on_delete=models.CASCADE)
    tanggal_dilihat = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.user.username} - {self.resep.judul}"

class Catatan(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    resep = models.ForeignKey(Resep, on_delete=models.CASCADE)
    isi_catatan = models.TextField()
    tanggal_dibuat = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"Catatan {self.user.username} di {self.resep.judul}"