from django.shortcuts import render

# Create your views here.
from rest_framework import viewsets
from rest_framework.permissions import IsAuthenticated

from .models import FuelStation
from .serializers import FuelStationSerializer


class FuelStationViewSet(viewsets.ModelViewSet):

    queryset = FuelStation.objects.all()
    serializer_class = FuelStationSerializer
    permission_classes = [IsAuthenticated]