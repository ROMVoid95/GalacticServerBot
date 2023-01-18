package io.github.romvoid95.util.discord;

import java.util.Optional;

import net.dv8tion.jda.api.entities.emoji.Emoji;

public enum Emojis
{

    ERROR(":x:", "\u274C"),
    ERROR2(":x:", "\u274C"),
    DICE(":game_die:", "\uD83C\uDFB2"),
    SAD(":frowning:", "\uD83D\uDE26"),
    CORRECT(":white_check_mark:", "\u2705"),
    OK(":ok_hand:", "\uD83D\uDC4C"),
    STOP(":octagonal_sign:", "\uD83D\uDED1"),
    TALKING(":speech_balloon:", "\uD83D\uDCAC"),
    CRYING(":sob:", "\uD83D\uDE2D"),
    WARNING(":warning:", "\u26a0"),
    POPPER(":tada:", "\uD83C\uDF89"),
    ZAP(":zap:", "\u26a1"),
    MEGA(":mega:", "\uD83D\uDCE3"),
    CLOCK(":clock1:", "\uD83D\uDD50"),
    CONFUSED(":confused:", "\uD83D\uDE15"),
    WORRIED(":worried:", "\uD83D\uDE1F"),
    THINKING(":thinking:", "\uD83E\uDD14"),
    STOPWATCH(":stopwatch:", "\u23f1"),
    BUY(":inbox_tray:", "\uD83D\uDCE5"),
    SELL(":outbox_tray:", "\uD83D\uDCE4"),
    MARKET(":shopping_car:", "\uD83D\uDED2"),
    MONEY(":money_bag:", "\uD83D\uDCB0"),
    PENCIL(":pencil:", "\uD83D\uDCDD"),
    SMILE(":smile:", "\uD83D\uDE04"),
    PICK(":pick:", "\u26cf"),
    HEART(":heart:", "\u2764"),
    BLUE_HEART(":blue_heart:", "\uD83D\uDC99"),
    RUNNER(":runner:", "\uD83C\uDFC3"),
    POTION1(":milk:", "\uD83E\uDD5B"),
    POTION2(":champagne:", "\uD83C\uDF7E"),
    CREDITCARD(":credit_card:", "\uD83D\uDCB3"),
    POUCH(":pouch:", "\uD83D\uDC5D"),
    REP(":military_medal:", "\uD83C\uDF96"),
    MAGAZINE(":newspaper:", "\uD83D\uDCF0"),
    AXE(":hammer_pick:", "\u2692"),
    DOLLAR(":dollar:", "\uD83D\uDCB5"),
    WOOD(":bamboo:", "\uD83C\uDF8D"),
    EYES(":eyes:", "\uD83D\uDC40"),
    PENNY(":cd:", "\uD83D\uDCBF"),
    RING(":ring:", "\uD83D\uDC8D"),
    WIND(":wind_blowing_face:", "\uD83C\uDF2C"),
    BOOSTER(":runner:", "\uD83C\uDFC3"),
    JOY(":joy:", "\uD83D\uDE02"),
    CROSSED_SWORD(":crossed_sword:", "\u2694"),
    MAG(":mag_right:", "\uD83D\uDD0E"),
    KEY(":key:", "\uD83D\uDD11"),
    DOG(":dog:", "\uD83D\uDC36"),
    CAT(":cat:", "\uD83D\uDC31"),
    HAMSTER(":hamster:", "\ud83d\udc39"),
    DOOR(":door:", "\uD83D\uDEAA"),
    LOVE_LETTER(":love_letter:", "\uD83D\uDC8C"),
    NECKLACE(":prayer_beads:", "\uD83D\uDCFF"),
    DIAMOND(":gem:", "\uD83D\uDC8E"),
    TUXEDO(":man_in_tuxedo:", "\uD83E\uDD35"),
    DRESS(":dress:", "\uD83D\uDC57"),
    COOKIE(":cookie:", "\uD83C\uDF6A"),
    CHOCOLATE(":chocolate_bar:", "\uD83C\uDF6B"),
    CLOTHES(":shirt:", "\uD83D\uDC55"),
    SHOES(":athletic_shoe:", "\uD83D\uDC5F"),
    ROSE(":rose:", "\uD83C\uDF39"),
    PARTY(":tada:", "\uD83C\uDF89"),
    STAR(":star:", "\u2b50"),
    GLOWING_STAR(":star2:", "\uD83C\uDF1F"),
    HOUSE(":house:", "\uD83C\uDFE0"),
    CAR(":red_car:", "\uD83D\uDE97"),
    MARKER(":large_orange_diamond:", "\uD83D\uDD36"),
    BLUE_SMALL_MARKER(":small_blue_diamond:", "\uD83D\uDD39"),
    DUST(":dash:", "\uD83D\uDCA8"),
    TROPHY(":trophy:", "\uD83C\uDFC6"),
    WRENCH(":wrench:", "\ud83d\udd27"),
    WHITE_CIRCLE(":white_circle:", "\u25CB"),
    MUSICAL_NOTE(":musical_note", "\uD83C\uDFB5"),
    DROPLET(":droplet:", "\uD83D\uDCA7"),
    DEV(":desktop:", "\uD83D\uDDA5"),
    ROCK(":rock:", "\uD83E\uDEA8"),
    FORK(":fork_and_knife:", "\uD83C\uDF74"),
    CALENDAR(":calendar:", "\uD83D\uDCC6"),
    CALENDAR2(":calendar_spiral:", "\uD83D\uDDD3\uFE0F"),
    SATELLITE(":satellite:", "\uD83D\uDCE1"),
    SLIDER(":level_slider:", "\uD83C\uDF9A\uFE0F"),
    WIDTH(":left_right_arrow:", "\u2194\uFE0F"),
    HEIGHT(":arrow_up_down:", "\u2195\uFE0F");

    final String discordNotation;
    final String unicode;

    Emojis(String discordNotation, String unicode)
    {
        this.discordNotation = discordNotation;
        this.unicode = unicode;
    }

    @Override
    public String toString()
    {
        return Optional.ofNullable(unicode).orElse(discordNotation) + " ";
    }

    public String toHeaderString()
    {
        return this + "\u2009";
    }

    public String getDiscordNotation()
    {
        return discordNotation;
    }

    public String getUnicode()
    {
        return unicode;
    }

    public Emoji getEmoji()
    {
        return Emoji.fromFormatted(unicode);
    }
}
