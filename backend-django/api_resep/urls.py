from django.urls import path
from . import views

urlpatterns = [
    # Fitur Auth (Login & Register)
    path('register/', views.register_user, name='register'),
    path('login/', views.login_user, name='login'),

    # Fitur Resep & Riwayat
    path('search/', views.search_resep, name='search_resep'),
    path('detect/', views.detect_and_generate, name='detect_and_generate'),
    path('generate-text/', views.generate_from_text, name='generate_from_text'),
    path('riwayat/', views.get_riwayat, name='get_riwayat'),
    path('riwayat/hapus/<int:resep_id>/', views.hapus_riwayat, name='hapus_riwayat'), 

    # Fitur Catatan & Deteksi Saja
    path('detect-only/', views.detect_only, name='detect_only'), # <-- SUDAH DIPERBAIKI
    path('catatan/', views.get_catatan, name='get_catatan'),
    path('catatan/tambah/', views.tambah_catatan, name='tambah_catatan'),
    path('catatan/edit/<int:catatan_id>/', views.edit_catatan, name='edit_catatan'), 
    path('catatan/hapus/<int:catatan_id>/', views.hapus_catatan, name='hapus_catatan'), 
]