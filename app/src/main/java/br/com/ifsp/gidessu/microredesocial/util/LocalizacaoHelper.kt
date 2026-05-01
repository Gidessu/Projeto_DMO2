package br.com.ifsp.gidessu.microredesocial.util

import android.Manifest
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.io.IOException
import java.util.Locale

class LocalizacaoHelper(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
) {
    interface Callback {
        fun onLocalizacaoRecebida(endereco: Address, latitude: Double, longitude: Double)
        fun onErro(mensagem: String)
    }

    @RequiresPermission(anyOf = [
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ])
    fun obterLocalizacaoAtual(callback: Callback) {
        val locationRequest = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMaxUpdateAgeMillis(0)
            .build()

        fusedLocationClient.getCurrentLocation(locationRequest, null)
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    obterEndereco(location.latitude, location.longitude, callback)
                } else {
                    callback.onErro("Localização indisponível: verifique se o GPS está ligado.")
                }
            }
            .addOnFailureListener { e ->
                callback.onErro("Erro ao buscar localização: ${e.message}")
            }
    }

    private fun obterEndereco(latitude: Double, longitude: Double, callback: Callback) {
        val geocoder = Geocoder(context, Locale.getDefault())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(latitude, longitude, 1, object : Geocoder.GeocodeListener {
                override fun onGeocode(address: MutableList<android.location.Address>) {
                    if (address.isNotEmpty()) {
                        callback.onLocalizacaoRecebida(address[0], latitude, longitude)
                    } else {
                        callback.onErro("Endereço não encontrado")
                    }
                }
                override fun onError(errorMessage: String?) {
                    callback.onErro(errorMessage ?: "Erro no Geocoder")
                }
            })
        } else {
            try {
                @Suppress("DEPRECATION")
                val address = geocoder.getFromLocation(latitude, longitude, 1)

                if (!address.isNullOrEmpty()) {
                    callback.onLocalizacaoRecebida(address[0], latitude, longitude)
                } else {
                    callback.onErro("Endereço não encontrado")
                }
            } catch (e: IOException) {
                callback.onErro("Falha na rede ao buscar endereço")
            }
        }
    }
}