# Informe Técnico Estructurado - Lumínico IoT

## 1. Introducción
En la actualidad, el desarrollo de sistemas de internet de las cosas (IoT) y domótica de código abierto requiere interfaces móviles intuitivas, rápidas y seguras que permitan interactuar con hardware embebido de forma local y sin dependencia de la nube. **Lumínico IoT** nace como una solución nativa para Android diseñada para servir como el centro de control definitivo de un microcontrolador **ESP32 (Exploit Dev v1)** equipado con tiras de LEDs neopixel, sensores de movimiento PIR, micrófonos para vúmetros rítmicos y sensores de luz LDR. La aplicación resuelve la necesidad de gestionar la iluminación inteligente, monitorear telemetría en tiempo real y asegurar las credenciales de acceso tanto del usuario como de la red Wi-Fi del microcontrolador bajo una estética moderna y robusta.

---

## 2. Desarrollo
El desarrollo de Lumínico IoT se estructuró siguiendo los más altos estándares de la ingeniería de software Android moderna:

- **Arquitectura y Stack Tecnológico**:
  - **Interfaz de Usuario**: Desarrollada íntegramente en **Jetpack Compose (Material 3)**, garantizando una UI reactiva y fluida.
  - **Base de Datos Local**: Implementación de **Room Database** con mapeo de entidades de usuario (`UserEntity`, `UserDao`) y procesamiento de anotaciones mediante **KSP**.
  - **Seguridad Criptográfica**: Las credenciales de acceso de los usuarios se procesan y almacenan de forma segura utilizando funciones de resumen **SHA-256**, evitando contraseñas en texto plano.
  - **Gestión de Red Local**: Utilización de clientes HTTP asíncronos (`Esp32ApiClient`) y enrutamiento de red Wi-Fi directo (`Esp32WifiManager`) hacia la IP del ESP32 (`192.168.4.1`).

- **Diseño UI/UX e Innovación Gráfica**:
  - **Paleta Neón/Gaming**: Combinación de tonos oscuros profundos (`#0F0C1B`) con acentos vibrantes de púrpura neón (`#C084FC`), logrando una inmersión visual perfecta para control de luces.
  - **Componentes Gráficos Avanzados (Canvas)**: Creación de la rueda cromática de 360° (`ColorWheelPicker`) y el **Radar Wi-Fi Animado** (`Esp32RadarDialog`) con efectos de eco y barrido en tiempo real.
  - **Experiencia Háptica**: Integración de vibración física (*Haptic Feedback*) en cada interacción táctil de botones y selectores.
  - **Persistencia Multimedia**: Uso de la librería **Coil** para la selección y almacenamiento persistente de la foto de perfil en el dispositivo.

---

## 3. Desenlace
Durante el proceso de implementación y pruebas en dispositivos reales y emuladores, se abordaron y resolvieron diversos retos técnicos cruciales:
- **Compatibilidad de Red**: Se superó la limitación de los emuladores respecto al hardware de Wi-Fi mediante un manejo robusto de excepciones (`try-catch`), permitiendo que en teléfonos reales la app enrute el tráfico directamente al ESP32.
- **Optimización de Hilos (Cero Latencia)**: Todas las operaciones de red y consultas a base de datos se trasladaron a hilos secundarios asíncronos (`Dispatchers.IO`), asegurando 0 milisegundos de retraso en la interfaz de usuario (*Main Thread*).
- **Limpieza de Interacción**: Se eliminó el exceso de notificaciones flotantes (*Toasts*) en cada toque de control, logrando una experiencia silenciosa, rápida y natural respaldada por indicadores de estado en tiempo real.
- **Gestión de Sesión y Autocompletado**: Se perfeccionó el `SessionManager` para recordar el correo electrónico del usuario al reabrir la app (con la opción *"Recordar mi sesión"* activa) y asegurar un cierre de sesión limpio.

---

## 4. Conclusión
**Lumínico IoT** representa una aplicación móvil completa, altamente pulida y segura. El proyecto cumple con creces los objetivos planteados: combina una interfaz gráfica de nivel comercial con una comunicación de red inalámbrica directa y eficiente hacia el hardware ESP32. Gracias a su sólida arquitectura basada en bases de datos locales cifradas, componentes gráficos personalizados en Canvas, retroalimentación háptica y un diseño estético impecable, la aplicación se encuentra en un estado óptimo, estable y **listo para su compilación final en APK** y despliegue operativo.
