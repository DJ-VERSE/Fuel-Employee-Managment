from rest_framework.routers import DefaultRouter
from .views import FuelStationViewSet


router = DefaultRouter()

router.register(
    r"stations",
    FuelStationViewSet
)

urlpatterns = router.urls