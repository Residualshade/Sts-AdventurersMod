package adventurers.characters;

import adventurers.Adventurers.Cleric;
import adventurers.Adventurers.Fighter;
import adventurers.Adventurers.Rogue;
import adventurers.Adventurers.Wizard;
import adventurers.AdventurersMod;
import adventurers.cards.starter.*;
import basemod.abstracts.CustomPlayer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.brashmonkey.spriter.Player;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import adventurers.Resources;
import adventurers.patches.*;
import adventurers.relics.*;

import java.util.ArrayList;

public class TheAdventurers extends CustomPlayer {

    public AdventurersBase[] adventurers = new AdventurersBase[4];
    //public MonsterGroup

    //CLASS INFO
    public static final String NAME = "The Adventurers";
    public static final String DESCRIPTION = "N/A";

    //CLASS STATS
    private static final int ENERGY_PER_TURN = 3;


    public static final int STARTING_HP = 4;
    public static final int MAX_HP = 4;
    public static final int STARTING_GOLD = 99;
    public static final int HAND_SIZE = 5;
    private static final int ORB_SLOTS = 0;

    public TheAdventurers(String name, PlayerClass setClass) {
        super(name, setClass, null, null, (String) null, null);

        initializeClass(
            null
            , Resources.SHOULDER_2_PATH
            , Resources.SHOULDER_1_PATH
            , Resources.CORPSE_PATH //probably need to dummy this somewhere
            , getLoadout()
            ,-4.0F
            ,-16.0F
            ,220.0F
            ,290.0F
            , new EnergyManager(ENERGY_PER_TURN)
        );
        loadAnimation(Resources.SKELETON_ATLAS_PATH,Resources.SKELETON_JSON_PATH,1.0F);
        AnimationState.TrackEntry trackEntry = state.setAnimation(0,"Idle", true);
        trackEntry.setTime(trackEntry.getEndTime()*MathUtils.random());

        Fighter fighter = new Fighter();
        fighter.healthBarUpdatedEvent();
        //fighter.usePreBattleAction();
        fighter.showHealthBar();
        Rogue rogue = new Rogue();
        rogue.healthBarUpdatedEvent();
        //rogue.usePreBattleAction();
        rogue.showHealthBar();
        Wizard wizard = new Wizard();
        wizard.healthBarUpdatedEvent();
        //wizard.usePreBattleAction();
        wizard.showHealthBar();
        Cleric cleric = new Cleric();
        cleric.healthBarUpdatedEvent();
        //cleric.usePreBattleAction();
        cleric.showHealthBar();

        adventurers[0] = fighter;
        adventurers[1] = rogue;
        adventurers[2] = wizard;
        adventurers[3] = cleric;
    }

    @Override
    public ArrayList<String> getStartingDeck() {
        ArrayList<String> startingDeck = new ArrayList<>();

        startingDeck.add(StrikeCleric.ID);
        startingDeck.add(DefendCleric.ID);

        startingDeck.add(StrikeFighter.ID);
        startingDeck.add(DefendFighter.ID);

        startingDeck.add(StrikeRogue.ID);
        startingDeck.add(DefendRogue.ID);

        startingDeck.add(StrikeWizard.ID);
        startingDeck.add(DefendWizard.ID);

        return startingDeck;
    }

    @Override
    public ArrayList<String> getStartingRelics() {
        ArrayList<String> startingRelics = new ArrayList<>();
        return startingRelics;
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        //super.render(spriteBatch);
        for (AdventurersBase adventurer : adventurers) {
            adventurer.render(spriteBatch);
        }
    }

    @Override
    public void update() {
        super.update();
        for (AdventurersBase adventurer : adventurers) {
            adventurer.update();
        }
    }

    @Override
    public void applyEndOfTurnTriggers() {
        super.applyEndOfTurnTriggers();
        for (AdventurersBase adventurer : adventurers) {
            adventurer.applyEndOfTurnTriggers();
            adventurer.powers.forEach(power -> power.atEndOfRound());
        }
    }

    @Override
    public void applyStartOfTurnPostDrawPowers() {
        super.applyStartOfTurnPostDrawPowers();
        for (AdventurersBase adventurer : adventurers) {
            adventurer.applyStartOfTurnPostDrawPowers();
        }
    }

    @Override
    public void applyStartOfTurnPowers() {
        super.applyStartOfTurnPowers();
        for (AdventurersBase adventurer : adventurers) {
            adventurer.applyStartOfTurnPowers();
            adventurer.loseBlock();
        }
    }

    @Override
    public void applyTurnPowers() {
        super.applyTurnPowers();
        for (AdventurersBase adventurer : adventurers) {
            adventurer.applyTurnPowers();
        }
    }

    @Override
    public void updatePowers() {
        super.updatePowers();
        for (AdventurersBase adventurer : adventurers) {
            adventurer.updatePowers();
        }
    }


    @Override
    public CharSelectInfo getLoadout() {
        return new CharSelectInfo(NAME, DESCRIPTION,STARTING_HP,MAX_HP,ORB_SLOTS,STARTING_GOLD,HAND_SIZE,this,getStartingRelics(),getStartingDeck(),false);
    }

    @Override
    public String getTitle(PlayerClass playerClass) {
        return NAME;
    }

    @Override
    public AbstractCard.CardColor getCardColor() {
        return AbstractCardEnum.ADVENTURERS_GOLD;
    }

    @Override
    public Color getCardRenderColor() {
        return AdventurersMod.MOD_COLOR;
    }

    @Override
    public AbstractCard getStartCardForEvent() {
        return new StrikeFighter();
    }

    @Override
    public Color getCardTrailColor() {
        return AdventurersMod.MOD_COLOR;
    }

    @Override
    public int getAscensionMaxHPLoss() {
        return 4;
    }

    @Override
    public BitmapFont getEnergyNumFont() {
        return FontHelper.energyNumFontRed;
    }

    @Override
    public void doCharSelectScreenSelectEffect() {
        CardCrawlGame.sound.playA("ATTACK_HEAVY", MathUtils.random(-0.2f, 0.2f));
        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT, true);
    }

    @Override
    public String getCustomModeCharacterButtonSoundKey() {
        return "ATTACK_HEAVY";
    }

    @Override
    public String getLocalizedCharacterName() {
        return NAME;
    }

    @Override
    public AbstractPlayer newInstance() {
        return new TheAdventurers(NAME, TheAdventurersEnum.THE_ADVENTURERS_ENUM);
    }

    @Override
    public String getSpireHeartText() {
        return "NL You ready your Weapon...";
    }

    @Override
    public Color getSlashAttackColor() {
        return AdventurersMod.MOD_COLOR;
    }

    @Override
    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
        return new AbstractGameAction.AttackEffect[]{
            AbstractGameAction.AttackEffect.SLASH_DIAGONAL
            , AbstractGameAction.AttackEffect.SLASH_HORIZONTAL
            , AbstractGameAction.AttackEffect.SLASH_VERTICAL
            , AbstractGameAction.AttackEffect.SLASH_HEAVY
        };
    }

    //TODO: Character Specific Dialog
    @Override
    public String getVampireText() {
        return "Navigating an unlit street, you come across several hooded figures in the midst of some dark ritual. As you approach, they turn to you in eerie unison. The tallest among them bares fanged teeth and extends a long, pale hand towards you. NL ~\"Join~ ~us,~ ~oh Mighty Warrior,~ ~and~ ~feel~ ~the~ ~warmth~ ~of~ ~the~ ~Spire.\"~";
    }
}
