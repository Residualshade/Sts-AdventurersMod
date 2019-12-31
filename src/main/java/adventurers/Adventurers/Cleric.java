package adventurers.Adventurers;

import adventurers.Resources;
import adventurers.characters.AdventurersBase;
import adventurers.characters.AltAdventurersBase;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ShaderHelper;
import com.megacrit.cardcrawl.helpers.SlimeAnimListener;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.RestRoom;
import kobting.friendlyminions.monsters.AbstractFriendlyMonster;

import static com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase.COMBAT;

public class Cleric extends AdventurersBase {

    public static String NAME = "Cleric";
    public static String ID = "Adventurers:Cleric";
    public static final int HP_MAX = 30;
    public static final int HB_X = 5;
    public static final int HB_Y = -10;
    public static final int HB_W = 200;
    public static final int HB_H = 280;
    public static final String IMG = "";
    public static final int OFFSET_X = -1050;
    public static final int OFFSET_Y = -50;

    public Cleric() {
        super(NAME, ID, HP_MAX, HB_X, HB_Y, HB_W, HB_H, "images/monsters/theCity/chosen/chosen.png", OFFSET_X, OFFSET_Y);
        loadAnimation("images/monsters/theCity/chosen/skeleton.atlas", "images/monsters/theCity/chosen/skeleton.json", 1.0F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        flipHorizontal = true;
    }
}
