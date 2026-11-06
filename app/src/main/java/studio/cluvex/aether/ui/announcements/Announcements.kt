package studio.cluvex.aether.ui.announcements

/**
 * The announcements shown in the "What's new" dialog and kept in
 * Settings → پیام‌ها و اطلاعیه‌ها.
 *
 * ## Inline markup used in the strings below
 *
 *  * `**…**`  strong (pure white, bold)
 *  * `*…*`    emphasis (italic)
 *  * `{…}`    a LEFT-TO-RIGHT island - the Compose equivalent of `<bdi>` /
 *             `<span dir="ltr">`. It is rendered between U+2066 LEFT-TO-RIGHT
 *             ISOLATE and U+2069 POP DIRECTIONAL ISOLATE, so English words,
 *             protocol names and chain arrows (`{Config → Psiphon}`) are laid out
 *             LTR internally while the brackets and punctuation around them keep
 *             the Persian paragraph's direction. EVERY Latin run goes in braces.
 *  * `\n`     a line break inside the same block (the HTML `<br>`).
 *
 * To publish news for a new version, add an [Announcement] at the TOP of [all].
 */
data class Announcement(
    /** The versionName this announcement belongs to, e.g. "1.5.0". */
    val version: String,
    /** Short title for the archive list. */
    val shortTitle: String,
    val blocks: List<AnnouncementBlock>,
)

sealed interface AnnouncementBlock {
    /** `<h2>` / `<h3>` in the accent colour. */
    data class Heading(val text: String, val large: Boolean = false) : AnnouncementBlock

    /** A paragraph; [note] is the smaller, muted `#94a3b8` hint style. */
    data class Paragraph(val text: String, val note: Boolean = false) : AnnouncementBlock

    /** `<ul>`. */
    data class Bullets(val items: List<String>) : AnnouncementBlock

    /** `<ol>`, numbered with Persian digits. */
    data class Numbered(val items: List<String>) : AnnouncementBlock

    /** The amber "important" box with the start-side (right, in RTL) border. */
    data class Warning(val text: String) : AnnouncementBlock
}

object Announcements {

    private val v150 = Announcement(
        version = "1.5.0",
        shortTitle = "🚀 پشتیبانی از کانفیگ‌های سفارشی و زنجیره‌سازی اتصالات",
        blocks = listOf(
            AnnouncementBlock.Heading(
                "🚀 قابلیت جدید و بزرگ: پشتیبانی از کانفیگ‌های سفارشی ({Custom Configs})",
                large = true,
            ),
            AnnouncementBlock.Paragraph(
                "سلام به همهٔ همراهان عزیز! در آپدیت جدید فیلترشکن، قابلیت جذاب و کاربردی " +
                    "**کانفیگ سفارشی** اضافه شده است.",
            ),
            AnnouncementBlock.Paragraph(
                "از این به بعد می‌توانید تمام کانفیگ‌های دلخواه خودتان را مستقیماً وارد برنامه کنید " +
                    "و دیگر هیچ نیازی به برنامه‌های جداگانه مثل {v2rayNG} یا {Npv Tunnel} نخواهید داشت:",
            ),
            AnnouncementBlock.Bullets(
                listOf(
                    "📁 **پشتیبانی کامل از فایل‌ها و لینک‌های نپستر ({.npvt})**",
                    "⚡ **پشتیبانی از انواع کانفیگ‌های {V2Ray}** (شامل پروتکل‌های {VLESS}، {VMess}، " +
                        "{Trojan}، {Shadowsocks} و به‌ویژه کانفیگ‌های پنل محبوب {BPB})",
                    "📋 ورود بسیار راحت با کپی لینک، انتخاب فایل و قابلیت کشیدن و رها کردن ({Drag & Drop}).",
                ),
            ),
            AnnouncementBlock.Warning(
                "⚠️ **یک نکته بسیار مهم قبل از وارد کردن کانفیگ:**\n" +
                    "حتماً و حتماً قبل از این‌که کانفیگی را به برنامه اضافه کنید، مطمئن شوید که سرور آن کانفیگ " +
                    "**روشن، سالم و فعال** است. اگر سرور یک کانفیگ از ریشه خاموش یا قطع شده باشد، برنامه " +
                    "معجزه نمی‌کند و در هیچ حالتی وصل نخواهد شد! پس اول از زنده بودن سرورتان مطمئن شوید، " +
                    "بعد آن را وارد کنید.",
            ),

            AnnouncementBlock.Heading("🔗 زنجیره‌سازی اتصالات ({Chaining})؛ دور زدن فیلترینگ‌های سنگین!"),
            AnnouncementBlock.Paragraph(
                "بزرگ‌ترین مزیت این قابلیت، امکان **ترکیب کانفیگ شخصی با موتورهای داخلی اتر، سایفون و تور** است:",
            ),
            AnnouncementBlock.Numbered(
                listOf(
                    "🛡️ **اگر سایفون یا تور در اپراتور شما مسدود است:**\n" +
                        "می‌توانید کانفیگ سالم خود را در ابتدای مسیر بگذارید (مثل حالت {Config → Psiphon}). " +
                        "کانفیگ شما فیلترینگ اولیه را رد می‌کند و در خروجی اینترنت آزاد و نامحدود سایفون را " +
                        "تحویل می‌گیرید!",
                    "🔄 **اگر کانفیگ خودتان فیلتر شده است:**\n" +
                        "می‌توانید از حالت زنجیرهٔ معکوس (مثل {Aether → Config} یا {Tor → Config}) استفاده کنید؛ " +
                        "اول موتورهای اتر یا تور فیلترینگ را دور می‌زنند و در نهایت خروجی از آی‌پی تمیز کانفیگ " +
                        "خودتان رد می‌شود.",
                    "🔀 **۱۵ حالت زنجیره‌ای متنوع (تکی، دوتایی و سه‌تایی):**\n" +
                        "دست شما کاملاً باز است تا با توجه به وضعیت اینترنت، بهترین چینش را انتخاب کنید.",
                ),
            ),
            AnnouncementBlock.Paragraph(
                "💡 *نکته دربارهٔ حالت‌های سه‌تایی:* در زنجیره‌های ۳ لایه (مثلاً {Config → Tor → Psiphon})، " +
                    "چون ترافیک از ۳ لایه رمزنگاری و تونل عبور می‌کند، زمان اتصال اولیه طولانی‌تر بوده و پینگ " +
                    "بالاتر می‌رود؛ پس فقط در صورت نیاز شدید از حالت‌های سه‌تایی استفاده کنید.",
                note = true,
            ),

            AnnouncementBlock.Heading("⏳ صبور بودن در اولین اتصال!"),
            AnnouncementBlock.Paragraph(
                "چه در ۶ اتصال اصلی برنامه و چه در حالت‌های زنجیره‌ای (به‌ویژه هر حالتی که **{Tor}** داخل آن است):",
            ),
            AnnouncementBlock.Bullets(
                listOf(
                    "⏱️ **اتصال بار اول ممکن است بین ۳ تا ۴ دقیقه طول بکشد!** چرا؟ چون تور و موتورها برای " +
                        "بار نخست باید مشخصات شبکه، دایرکتوری و رله‌ها را دریافت و کش (ذخیره) کنند. لطفاً دکمه " +
                        "را پشت‌سر‌هم نزنید و کمی صبور باشید.",
                    "⚡ **از دفعات دوم به بعد:** چون اطلاعات ذخیره شده، اتصال بسیار سریع‌تر (معمولاً " +
                        "**زیر ۱ دقیقه**) برقرار خواهد شد.",
                ),
            ),

            AnnouncementBlock.Heading("📶 تفاوت شدید اپراتورها و روش قلق‌گیری در ایران"),
            AnnouncementBlock.Paragraph(
                "شرایط فیلترینگ و شدت اختلالات {DPI} در ایران یکدست نیست؛ وضعیت اینترنت همراه اول با " +
                    "ایرانسل، مخابرات یا شرکت‌های خصوصی (مثل شاتل و آسیاتک) فرق دارد و حتی منطقه به منطقه یا " +
                    "شهر به شهر متفاوت است!",
            ),
            AnnouncementBlock.Bullets(
                listOf(
                    "✅ **تجربهٔ تستی ما:** برنامه روی تنظیمات پیش‌فرض (هم ۶ حالت اصلی و هم زنجیره‌ها) روی " +
                        "**همراه اول و آسیاتک** بدون مشکل متصل شد؛ اما ممکن است در منطقه یا اپراتور شما نیاز " +
                        "به تغییرات باشد.",
                    // The settings page is really called «انتقال و ضد-DPI» in this
                    // app (R.string.section_transport), so the text points there.
                    "🛡️ **تنظیمات ضد {DPI}:** اگر در حالت عادی وصل نشدید، وارد تنظیمات «انتقال و ضد-{DPI}» " +
                        "برنامه شوید و روش‌های مبهم‌سازی را امتحان کنید.",
                ),
            ),

            AnnouncementBlock.Heading("🛠 اگر به هیچ وجه متصل نشدید چه کنید؟"),
            AnnouncementBlock.Numbered(
                listOf(
                    "✈️ **ریست آی‌پی و شبکه:** در گوشی، دستگاه را ۲ تا ۳ دقیقه روی **حالت پرواز " +
                        "({Airplane Mode})** بگذارید؛ در اینترنت خانگی، مودم وای‌فای را ۳ دقیقه کاملاً خاموش " +
                        "کرده و مجدداً روشن کنید تا {IP} شما تغییر کند.",
                    "🔄 **تست و خطا:** گزینه‌های مختلف {DPI} را در برنامه تغییر دهید یا کانفیگ‌های اختصاصی " +
                        "سالم دیگری را در زنجیره‌ها تست کنید.",
                    "🚪 **کلام آخر و واقع‌بینانه:** اگر تمام حالت‌ها، کانکشن‌های اختصاصی، تنظیمات {DPI} و " +
                        "تغییرات شبکه را انجام دادید و باز هم نتیجه نگرفتید، متأسفانه پروتکل‌ها روی دکل یا مرکز " +
                        "مخابراتی منطقهٔ شما دچار اختلال شدید زیرساختی هستند و در آن لحظه باید سراغ فیلترشکن‌ها " +
                        "و {VPN}‌های دیگر بروید.",
                ),
            ),
        ),
    )

    /** Newest first. */
    val all: List<Announcement> = listOf(v150)

    /** What the automatic dialog shows: this version's news, else the newest. */
    fun forVersion(versionName: String): Announcement =
        all.firstOrNull { it.version == versionName } ?: all.first()
}
