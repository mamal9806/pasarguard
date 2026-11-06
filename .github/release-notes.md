<div dir="ltr" align="left">

## Aether 1.5.0

Installs over your current Aether (same signing key, no uninstall needed).
App 1.5.0 · version code 18 · engine core 2.3.0.

### New: custom configs (V2Ray / Xray and NapsternetV)

**Settings → Custom configs** runs your own configs, alone or chained with the built-in engine.

- **Import:** paste from the clipboard (one or many links, links inside a message, base64 or `https://` subscriptions, Xray JSON), pick a file (`.npvt`, `.txt`, `.json`, QR image), scan a QR code with the camera, or read a QR code from an image.
- **Protocols:** VLESS (REALITY, XTLS Vision), VMess, Trojan, Shadowsocks, SOCKS, WireGuard; transports TCP, WebSocket, gRPC, HTTPUpgrade, XHTTP, mKCP with TLS or REALITY.
- **NapsternetV** `.npvt` files and `npvt://` links, with the author's lock, password, expiry and "mobile data only" rules respected.
- **Saved configs:** select, edit, rename, copy link, delete; stored encrypted.
- **Real delay test** per config or for all configs, measured *through* the config in Xray-core (like v2rayNG), with **No response** / **Server off** / **Filtered** / **Invalid config** diagnosis.
- **15 modes:** the config alone, or in front of / behind the engine:
  `Config → Aether`, `Aether → Config`, `Config → Psiphon`, `Psiphon → Config`, `Config → Tor`, `Tor → Config`,
  `Config → Aether → Psiphon`, `Config → Aether → Tor`, `Config → Tor → Psiphon`, `Config → Tor → Aether`,
  `Aether → Psiphon → Config`, `Aether → Tor → Config`, `Tor → Psiphon → Config`, `Tor → Aether → Config`.
- The home screen shows the running chain, e.g. `VLESS → Aether(WireGuard)`.
- Configs run in the bundled Xray-core (`libxray.so`); with Custom configs off, every session works as before.
- New permission: camera, requested only when scanning a QR code.

### Engine core 2.3.0

- Chrome-like TLS fingerprint on tunnel handshakes; ECH also covers the WARP API.
- IPv6-only domains resolve inside the tunnel.
- Gool: WireGuard over MASQUE by default; classic WireGuard over WireGuard is selectable.
- The engine ships with the C++ runtime it needs, and CI rejects an APK with a missing engine library.

### Fixes

- **Real delay test showed `-1` / Filtered for every config**, even ones that connect at once. The old test made its own TLS handshake with the phone's TLS stack, which a REALITY server never completes, without the config's fingerprint and with the network's DNS. The test now runs each config in Xray-core and times an HTTP request through it; results are written to the diagnostics log.
- **Chains with Tor in the middle or at the end** (`Config → Tor`, `Config → Tor → Aether`, `Config → Tor → Psiphon`, `Config → Aether → Tor`, `Tor → Config`):
  - Tor's relay handshake through a config no longer carries a made-up server name, which servers with traffic sniffing turned into a failed lookup (`tls handshake eof`).
  - Tor's directory download no longer gives up after 10 seconds on a slow chain.
  - Bridge attempts reuse the directory Tor already has instead of downloading it again.
  - Xray now closes half-closed connections after a short grace period instead of immediately.
  - Higher handshake and idle limits for chained hops; multiplexing for configs behind a slow hop; retries for transient Tor stream errors.
- **`Psiphon → Config`:** Psiphon is given the device's real DNS servers; disconnecting during the Psiphon start-up now stops cleanly.
- **Paste from clipboard** now inserts the text into the import box, including links shared from Telegram or a browser, and removes invisible characters around links.
- Accessibility: the connect button, widget and settings controls announce their action and state in TalkBack.
- Gemini: replacing or clearing the API key takes effect immediately.
- Spacing of *Delete all configs*; the QR scanner opens in portrait.

### Security audit: 92 / 100

Full report (English + Persian): `docs/SECURITY_AUDIT_1.5.0.md`.

- **Fixed:** trust-everything TLS check removed from the ping probe; Xray log lines no longer reveal the server or UUID of a locked NPVT config; subscription downloads go through the tunnel while connected; *Copy link* is marked sensitive on the clipboard; ping probe ports are password-protected.
- **Checked:** DNS and IPv6 (`::/0`) are routed into the tunnel, Tor modes carry DNS over TCP and drop UDP, WebRTC STUN leaves through the tunnel exit, the kill switch keeps a blocking TUN on engine failure.
- **Open:** local SOCKS ports used by chains have no password; LAN sharing has no password by default. Proxy mode and split-tunnel-excluded apps bypass the tunnel by design.

</div>

<div dir="rtl" align="right">

<h2 dir="rtl" align="right">‏Aether نسخهٔ ۱.۵.۰</h2>

<p dir="rtl" align="right">‏روی نسخهٔ فعلی نصب می‌شود (همان کلید امضا، بدون نیاز به حذف). برنامه <span dir="ltr">1.5.0</span> · کد نسخه <span dir="ltr">18</span> · هستهٔ موتور <span dir="ltr">2.3.0</span>.</p>

<h3 dir="rtl" align="right">‏جدید: کانفیگ‌های سفارشی (<span dir="ltr">V2Ray / Xray</span> و <span dir="ltr">NapsternetV</span>)</h3>

<p dir="rtl" align="right">‏بخش <strong>تنظیمات ← کانفیگ‌های سفارشی</strong> کانفیگ‌های خودتان را به‌تنهایی یا زنجیره‌ای با موتور داخلی اجرا می‌کند.</p>

<ul dir="rtl" align="right">
<li align="right"><strong>وارد کردن:</strong> چسباندن از کلیپ‌بورد (یک یا چند لینک، لینک‌های داخل یک پیام، اشتراک <span dir="ltr">base64</span> یا <span dir="ltr">https://</span>، کانفیگ <span dir="ltr">JSON</span>)، انتخاب فایل (<span dir="ltr">.npvt</span>، <span dir="ltr">.txt</span>، <span dir="ltr">.json</span>، تصویر <span dir="ltr">QR</span>)، اسکن کد <span dir="ltr">QR</span> با دوربین یا خواندن آن از تصویر.</li>
<li align="right"><strong>پروتکل‌ها:</strong> <span dir="ltr">VLESS</span> (<span dir="ltr">REALITY</span>، <span dir="ltr">XTLS Vision</span>)، <span dir="ltr">VMess</span>، <span dir="ltr">Trojan</span>، <span dir="ltr">Shadowsocks</span>، <span dir="ltr">SOCKS</span>، <span dir="ltr">WireGuard</span>؛ ترنسپورت‌های <span dir="ltr">TCP</span>، <span dir="ltr">WebSocket</span>، <span dir="ltr">gRPC</span>، <span dir="ltr">HTTPUpgrade</span>، <span dir="ltr">XHTTP</span> و <span dir="ltr">mKCP</span> با <span dir="ltr">TLS</span> یا <span dir="ltr">REALITY</span>.</li>
<li align="right"><strong><span dir="ltr">NapsternetV</span>:</strong> فایل <span dir="ltr">.npvt</span> و لینک <span dir="ltr">npvt://</span>، با رعایت قفل سازنده، رمز، تاریخ انقضا و «فقط اینترنت همراه».</li>
<li align="right"><strong>کانفیگ‌های ذخیره‌شده:</strong> انتخاب، ویرایش، تغییر نام، کپی لینک و حذف؛ به‌صورت رمزنگاری‌شده ذخیره می‌شوند.</li>
<li align="right"><strong>تست تأخیر واقعی</strong> برای هر کانفیگ یا همه با هم، از داخل خود کانفیگ در <span dir="ltr">Xray-core</span> (مثل <span dir="ltr">v2rayNG</span>)، با تشخیص «پاسخ نمی‌دهد»، «سرور خاموش»، «فیلترشده» یا «کانفیگ نامعتبر».</li>
<li align="right"><strong>۱۵ حالت:</strong> کانفیگ به‌تنهایی، یا جلو / پشت موتور: <span dir="ltr">Config → Aether</span>، <span dir="ltr">Aether → Config</span>، <span dir="ltr">Config → Psiphon</span>، <span dir="ltr">Psiphon → Config</span>، <span dir="ltr">Config → Tor</span>، <span dir="ltr">Tor → Config</span>، <span dir="ltr">Config → Aether → Psiphon</span>، <span dir="ltr">Config → Aether → Tor</span>، <span dir="ltr">Config → Tor → Psiphon</span>، <span dir="ltr">Config → Tor → Aether</span>، <span dir="ltr">Aether → Psiphon → Config</span>، <span dir="ltr">Aether → Tor → Config</span>، <span dir="ltr">Tor → Psiphon → Config</span>، <span dir="ltr">Tor → Aether → Config</span>.</li>
<li align="right">صفحهٔ اصلی زنجیرهٔ در حال اجرا را نشان می‌دهد؛ مثلاً <span dir="ltr">VLESS → Aether(WireGuard)</span>.</li>
<li align="right">کانفیگ‌ها در هستهٔ <span dir="ltr">Xray-core</span> همراه برنامه (<span dir="ltr">libxray.so</span>) اجرا می‌شوند؛ با خاموش بودن کانفیگ سفارشی، همه‌چیز مثل قبل کار می‌کند.</li>
<li align="right">مجوز تازه: دوربین، فقط هنگام اسکن کد <span dir="ltr">QR</span>.</li>
</ul>

<h3 dir="rtl" align="right">‏هستهٔ موتور <span dir="ltr">2.3.0</span></h3>

<ul dir="rtl" align="right">
<li align="right">اثر انگشت <span dir="ltr">TLS</span> شبیه کروم در دست‌دهی‌های تونل؛ <span dir="ltr">ECH</span> تماس‌های <span dir="ltr">API</span> ورپ را هم می‌پوشاند.</li>
<li align="right">دامنه‌هایی که فقط <span dir="ltr">IPv6</span> دارند داخل تونل resolve می‌شوند.</li>
<li align="right"><span dir="ltr">Gool</span>: به‌طور پیش‌فرض وایرگارد داخل <span dir="ltr">MASQUE</span>؛ حالت کلاسیک وایرگارد داخل وایرگارد قابل انتخاب است.</li>
<li align="right">کتابخانهٔ زمان اجرای <span dir="ltr">C++</span> لازم موتور همراه آن بسته‌بندی می‌شود و <span dir="ltr">CI</span> هر <span dir="ltr">APK</span> فاقد کتابخانهٔ موتور را رد می‌کند.</li>
</ul>

<h3 dir="rtl" align="right">‏رفع اشکال‌ها</h3>

<ul dir="rtl" align="right">
<li align="right"><strong>تست تأخیر واقعی برای همهٔ کانفیگ‌ها <span dir="ltr">-1</span> و «فیلترشده» نشان می‌داد</strong>، حتی کانفیگ‌هایی که فوراً وصل می‌شدند. تست قبلی با <span dir="ltr">TLS</span>ِ خود گوشی دست‌دهی جداگانه می‌کرد که سرور <span dir="ltr">REALITY</span> هرگز کاملش نمی‌کند، آن هم بدون اثر انگشت کانفیگ و با <span dir="ltr">DNS</span>ِ شبکه. حالا هر کانفیگ در <span dir="ltr">Xray-core</span> اجرا می‌شود و زمان یک درخواست <span dir="ltr">HTTP</span> از داخل آن اندازه گرفته می‌شود؛ نتیجه‌ها در گزارش عیب‌یابی ثبت می‌شوند.</li>
<li align="right"><strong>زنجیره‌هایی که <span dir="ltr">Tor</span> در میانه یا انتهای آن‌هاست</strong> (<span dir="ltr">Config → Tor</span>، <span dir="ltr">Config → Tor → Aether</span>، <span dir="ltr">Config → Tor → Psiphon</span>، <span dir="ltr">Config → Aether → Tor</span>، <span dir="ltr">Tor → Config</span>):
<ul dir="rtl" align="right">
<li align="right">دست‌دهی <span dir="ltr">Tor</span> با رله‌ها از مسیر کانفیگ دیگر نام سرور ساختگی نمی‌فرستد؛ سرورهای دارای <span dir="ltr">sniffing</span> آن را به جست‌وجوی ناموفق تبدیل می‌کردند (<span dir="ltr">tls handshake eof</span>).</li>
<li align="right">دریافت فهرست رله‌های <span dir="ltr">Tor</span> در زنجیرهٔ کند دیگر پس از ۱۰ ثانیه رها نمی‌شود.</li>
<li align="right">تلاش‌های اتصال با پل از فهرستی که <span dir="ltr">Tor</span> از قبل دارد استفاده می‌کنند و آن را دوباره دانلود نمی‌کنند.</li>
<li align="right">ایکس‌ری اتصال‌های نیمه‌بسته را پس از یک مهلت کوتاه می‌بندد، نه فوراً.</li>
<li align="right">مهلت‌های بیشتر برای دست‌دهی و بی‌کاری گام‌های زنجیره، مالتی‌پلکس برای کانفیگ پشت گام کند و تلاش دوباره برای خطاهای گذرای استریم <span dir="ltr">Tor</span>.</li>
</ul></li>
<li align="right"><strong><span dir="ltr">Psiphon → Config</span>:</strong> <span dir="ltr">DNS</span> واقعی دستگاه به سایفون داده می‌شود و قطع اتصال در حین راه‌اندازی سایفون به‌درستی انجام می‌شود.</li>
<li align="right"><strong>«چسباندن از کلیپ‌بورد»</strong> حالا متن را واقعاً در کادر وارد می‌کند، از جمله لینک‌های اشتراک‌گذاری‌شده از تلگرام یا مرورگر، و نویسه‌های نامرئی اطراف لینک‌ها را حذف می‌کند.</li>
<li align="right">دسترس‌پذیری: دکمهٔ اتصال، ویجت و کنترل‌های تنظیمات عمل و وضعیت خود را در <span dir="ltr">TalkBack</span> اعلام می‌کنند.</li>
<li align="right"><span dir="ltr">Gemini</span>: تعویض یا پاک کردن کلید <span dir="ltr">API</span> فوراً اعمال می‌شود.</li>
<li align="right">فاصلهٔ دکمهٔ «حذف همهٔ کانفیگ‌ها» اصلاح شد و اسکنر <span dir="ltr">QR</span> عمودی باز می‌شود.</li>
</ul>

<h3 dir="rtl" align="right">‏ممیزی امنیتی: ۹۲ از ۱۰۰</h3>

<p dir="rtl" align="right">‏گزارش کامل (فارسی و انگلیسی): <span dir="ltr">docs/SECURITY_AUDIT_1.5.0.md</span></p>

<ul dir="rtl" align="right">
<li align="right"><strong>رفع‌شده:</strong> بررسی <span dir="ltr">TLS</span>ِ «همه را بپذیر» از تست پینگ حذف شد؛ لاگ <span dir="ltr">Xray</span> دیگر سرور و <span dir="ltr">UUID</span>ِ کانفیگ قفل‌شدهٔ <span dir="ltr">NPVT</span> را نشان نمی‌دهد؛ دریافت اشتراک در حالت وصل از داخل تونل انجام می‌شود؛ «کپی لینک» در کلیپ‌بورد حساس علامت می‌خورد؛ پورت‌های تست پینگ رمز دارند.</li>
<li align="right"><strong>بررسی‌شده:</strong> <span dir="ltr">DNS</span> و <span dir="ltr">IPv6</span> (<span dir="ltr">::/0</span>) داخل تونل مسیریابی می‌شوند، در حالت‌های <span dir="ltr">Tor</span>، <span dir="ltr">DNS</span> روی <span dir="ltr">TCP</span> می‌رود و <span dir="ltr">UDP</span> دور ریخته می‌شود، <span dir="ltr">STUN</span>ِ <span dir="ltr">WebRTC</span> از خروجی تونل بیرون می‌رود و کیل‌سوییچ هنگام خطای موتور یک <span dir="ltr">TUN</span>ِ مسدودکننده نگه می‌دارد.</li>
<li align="right"><strong>باز:</strong> پورت‌های <span dir="ltr">SOCKS</span> محلی زنجیره‌ها رمز ندارند؛ اشتراک روی شبکهٔ محلی به‌طور پیش‌فرض رمز ندارد. حالت پراکسی و برنامه‌های مستثنا در تونل‌سازی جزئی طبق طراحی از تونل رد نمی‌شوند.</li>
</ul>

</div>
