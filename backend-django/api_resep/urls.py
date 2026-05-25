from django.urls import path
from . import views

urlpatterns = [
    path('search/', views.search_resep, name='search_resep'),
    path('riwayat/', views.kelola_riwayat, name='kelola_riwayat'),
]