package com.goosebumps.rider.ui.map

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.goosebumps.rider.BuildConfig

/**
 * Geoapify map rendered via Leaflet.js inside a WebView.
 *
 * @param lat          Center latitude
 * @param lng          Center longitude
 * @param zoom         Zoom level (1–20)
 * @param markerLat    Optional marker latitude
 * @param markerLng    Optional marker longitude
 * @param markerLabel  Popup label shown on the marker
 * @param routePoints  List of (lat, lng) pairs to draw a polyline route
 * @param modifier     Compose modifier
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun GeoapifyMapView(
    lat: Double,
    lng: Double,
    zoom: Int = 15,
    markerLat: Double? = null,
    markerLng: Double? = null,
    markerLabel: String = "",
    routePoints: List<Pair<Double, Double>> = emptyList(),
    modifier: Modifier = Modifier
) {
    val apiKey = BuildConfig.GEOAPIFY_API_KEY

    // Build the route polyline JS snippet
    val routeJs = if (routePoints.size >= 2) {
        val coords = routePoints.joinToString(",") { "[${it.first},${it.second}]" }
        """
        var routeLine = L.polyline([$coords], {
            color: '#FF6B35',
            weight: 5,
            opacity: 0.85,
            lineJoin: 'round'
        }).addTo(map);
        map.fitBounds(routeLine.getBounds(), { padding: [40, 40] });
        """.trimIndent()
    } else ""

    // Build the marker JS snippet
    val markerJs = if (markerLat != null && markerLng != null) {
        """
        var customIcon = L.divIcon({
            className: '',
            html: '<div style="background:#FF6B35;width:18px;height:18px;border-radius:50%;border:3px solid #fff;box-shadow:0 2px 6px rgba(0,0,0,0.5);"></div>',
            iconSize: [18, 18],
            iconAnchor: [9, 9]
        });
        L.marker([$markerLat, $markerLng], { icon: customIcon })
            .addTo(map)
            .bindPopup('${markerLabel.replace("'", "\\'")}');
        """.trimIndent()
    } else ""

    val html = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                * { margin: 0; padding: 0; box-sizing: border-box; }
                html, body, #map { width: 100%; height: 100%; background: #0D0D0D; }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                var map = L.map('map', {
                    zoomControl: false,
                    attributionControl: false
                }).setView([$lat, $lng], $zoom);

                L.tileLayer(
                    'https://maps.geoapify.com/v1/tile/dark-matter/{z}/{x}/{y}.png?apiKey=$apiKey',
                    { maxZoom: 20, minZoom: 3 }
                ).addTo(map);

                $markerJs
                $routeJs
            </script>
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                    setSupportZoom(true)
                    builtInZoomControls = false
                    displayZoomControls = false
                }
                webViewClient = WebViewClient()
                webChromeClient = WebChromeClient()
                setBackgroundColor(android.graphics.Color.parseColor("#0D0D0D"))
                loadDataWithBaseURL(
                    "https://maps.geoapify.com",
                    html,
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        },
        update = { webView ->
            // Reload when coordinates change
            webView.loadDataWithBaseURL(
                "https://maps.geoapify.com",
                html,
                "text/html",
                "UTF-8",
                null
            )
        }
    )
}
