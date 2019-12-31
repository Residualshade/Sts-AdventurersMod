package adventurers.Adventurers;

import adventurers.characters.AdventurersBase;
import adventurers.characters.AltAdventurersBase;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.SlimeAnimListener;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;
import kobting.friendlyminions.monsters.AbstractFriendlyMonster;

public class Wizard extends AdventurersBase {
    public static String NAME = "Wizard";
    public static String ID = "Adventurers:Wizard";
    public static final int HP_MAX = 30;
    public static final int HB_X = -8;
    public static final int HB_Y = 10;
    public static final int HB_W = 230;
    public static final int HB_H = 240;
    public static final int OFFSET_X = -1200;
    public static final int OFFSET_Y = 50;

    public Wizard() {
        super(NAME, ID, HP_MAX, HB_X, HB_Y, HB_W, HB_H, "images/characters/defect/idle/defect.png", OFFSET_X, OFFSET_Y);
        loadAnimation("images/characters/defect/idle/skeleton.atlas", "images/characters/defect/idle/skeleton.json", 1.0F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }
}
