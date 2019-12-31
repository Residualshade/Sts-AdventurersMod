package adventurers;

import adventurers.cards.starter.*;
import basemod.BaseMod;
import basemod.ModPanel;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import adventurers.cards.common.attack.AgileStrike;
import adventurers.cards.common.attack.Maim;
import adventurers.cards.common.skill.StoicGuard;
import adventurers.cards.common.skill.Feint;
import adventurers.cards.common.attack.VulnerableFinisher;
import adventurers.cards.rare.attack.FillerRareAttack;
import adventurers.cards.rare.power.FillerRarePower;
import adventurers.cards.common.skill.WeakFinisher;
import adventurers.cards.uncommon.attack.FillerUncommonAttack;
import adventurers.cards.uncommon.power.FillerUncommonPower;
import adventurers.cards.rare.skill.FillerRareSkill;
import adventurers.cards.uncommon.skill.FillerUncommonSkill;
import adventurers.characters.TheAdventurers;
import adventurers.patches.*;
import adventurers.relics.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;

@SpireInitializer
public class AdventurersMod implements PostInitializeSubscriber,EditCharactersSubscriber, EditCardsSubscriber, EditRelicsSubscriber, EditStringsSubscriber {

    public static final Logger logger = LogManager.getLogger(AdventurersMod.class.getName());

    //MOD INFO
    private static final String MOD_NAME = "The Adventurers";
    private static final String AUTHOR = "Residualshade";
    private static final String DESCRIPTOIN = "Adds The Adventurers as a new playable character.";

    public static final Color MOD_COLOR = Color.GOLD;

    public static final Color FIGHTER_COLOR = Color.RED;
    public static final Color ROGUE_COLOR = Color.GREEN;
    public static final Color WIZARD_COLOR = Color.BLUE;
    public static final Color CLERIC_COLOR = Color.WHITE;

    public AdventurersMod(){
        BaseMod.subscribe(this);

        logger.info("creating the color " + AbstractCardEnum.ADVENTURERS_GOLD.toString());
        BaseMod.addColor(
                AbstractCardEnum.ADVENTURERS_GOLD
                ,MOD_COLOR
                , Resources.BG_ATTACK_512_PATH
                , Resources.BG_SKILL_512_PATH
                , Resources.BG_POWER_512_PATH
                , Resources.ENERGY_ORB_512_PATH
                , Resources.BG_ATTACK_1024_PATH
                , Resources.BG_SKILL_1024_PATH
                , Resources.BG_POWER_1024_PATH
                , Resources.ENERGY_ORB_1024_PATH
                , Resources.ENERGY_ORB_IN_DESCRIPTION_PATH
        );
    }

    //Used by @SpireInitializer
    public static void initialize(){
        logger.debug("Started initializing " + MOD_NAME);
        AdventurersMod adventurersMod = new AdventurersMod();
        logger.debug("Finished initializing " + MOD_NAME);
    }

    @Override
    public void receiveEditCharacters() {
        logger.info("adding " + TheAdventurersEnum.THE_ADVENTURERS_ENUM.toString() + " character to game");
        BaseMod.addCharacter(
            new TheAdventurers(
                TheAdventurers.NAME
                , TheAdventurersEnum.THE_ADVENTURERS_ENUM
            )
            , Resources.BUTTON_PATH
            , Resources.PORTRAIT_PATH
            , TheAdventurersEnum.THE_ADVENTURERS_ENUM
        );
        logger.info("added " + TheAdventurersEnum.THE_ADVENTURERS_ENUM.toString() + " character to game");
    }

    @Override
    public void receiveEditCards() {
        logger.info("adding " + TheAdventurersEnum.THE_ADVENTURERS_ENUM.toString() + " cards to game");
        BaseMod.addCard(new StrikeFighter());
        BaseMod.addCard(new StrikeRogue());
        BaseMod.addCard(new StrikeWizard());
        BaseMod.addCard(new StrikeCleric());
        BaseMod.addCard(new DefendFighter());
        BaseMod.addCard(new DefendRogue());
        BaseMod.addCard(new DefendWizard());
        BaseMod.addCard(new DefendCleric());

        logger.info("added " + TheAdventurersEnum.THE_ADVENTURERS_ENUM.toString() + " cards to game");
    }

    @Override
    public void receiveEditRelics() {
        logger.info("adding " + TheAdventurersEnum.THE_ADVENTURERS_ENUM.toString() + " relics to game");
        logger.info("added " + TheAdventurersEnum.THE_ADVENTURERS_ENUM.toString() + " relics to game");
    }

    @Override
    public void receiveEditStrings() {
        logger.info("adding " + TheAdventurersEnum.THE_ADVENTURERS_ENUM.toString() + " card strings to game");
        String cardStrings = Gdx.files.internal(Resources.CARD_STRINGS_PATH).readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(CardStrings.class, cardStrings);
        String relicStrings = Gdx.files.internal(Resources.RELIC_STRINGS_PATH).readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(RelicStrings.class, relicStrings);
        logger.info("added " + TheAdventurersEnum.THE_ADVENTURERS_ENUM.toString() + " card strings to game");
    }

    @Override
    public void receivePostInitialize() {
        Texture badgeTexture = new Texture(Resources.MOD_BADGE_PATH);
        ModPanel settingsPanel = new ModPanel();
        BaseMod.registerModBadge(badgeTexture,MOD_NAME,AUTHOR,DESCRIPTOIN,settingsPanel);

        Settings.isDailyRun = false;
        Settings.isTrial = false;
        Settings.isDemo = false;
    }
}
