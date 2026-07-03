from django.contrib import admin
from .models import Resep, Catatan

# Mendaftarkan model agar muncul di halaman admin
@admin.register(Resep)
class ResepAdmin(admin.ModelAdmin):
    list_display = ('judul', 'pembuat', 'created_at') # Menampilkan kolom di tabel admin
    search_fields = ('judul',)

@admin.register(Catatan)
class CatatanAdmin(admin.ModelAdmin):
    list_display = ('judul', 'user', 'created_at')
    search_fields = ('judul', 'user__username')