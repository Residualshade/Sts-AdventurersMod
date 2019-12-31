package adventurers.characters;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTarget;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.ShaderHelper;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.ShaderHelper.Shader;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.BackAttackPower;
import com.megacrit.cardcrawl.powers.SlowPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.screens.stats.StatsScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BobEffect;
import com.megacrit.cardcrawl.vfx.DebuffParticleEffect;
import com.megacrit.cardcrawl.vfx.ShieldParticleEffect;
import com.megacrit.cardcrawl.vfx.TextAboveCreatureEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect;
import com.megacrit.cardcrawl.vfx.combat.BlockedWordEffect;
import com.megacrit.cardcrawl.vfx.combat.BuffParticleEffect;
import com.megacrit.cardcrawl.vfx.combat.DeckPoofEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashIntentEffect;
import com.megacrit.cardcrawl.vfx.combat.HbBlockBrokenEffect;
import com.megacrit.cardcrawl.vfx.combat.HealEffect;
import com.megacrit.cardcrawl.vfx.combat.StrikeEffect;
import com.megacrit.cardcrawl.vfx.combat.StunStarEffect;
import com.megacrit.cardcrawl.vfx.combat.UnknownParticleEffect;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AltAdventurersBase extends AbstractCreature {
    private static final Logger logger = LogManager.getLogger(AbstractMonster.class.getName());
    private static final float DEATH_TIME = 1.8F;
    private static final float ESCAPE_TIME = 3.0F;
    protected static final byte ESCAPE = 99;
    protected static final byte ROLL = 98;
    public float deathTimer;
    public Color bgColor;
    protected Texture img;
    public boolean tintFadeOutCalled;
    protected HashMap<Byte, String> moveSet;
    public boolean escaped;
    public boolean escapeNext;
    private float hoverTimer;
    public boolean cannotEscape;
    public ArrayList<DamageInfo> damage;
    private BobEffect bobEffect;

    public AltAdventurersBase(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        this(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY, false);
    }

    public AltAdventurersBase(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY, boolean ignoreBlights) {
        this.deathTimer = 0.0F;
        this.bgColor = new Color(1.0F, 1.0F, 1.0F, 1.0F);
        this.tintFadeOutCalled = false;
        this.moveSet = new HashMap();
        this.escaped = false;
        this.escapeNext = false;
        this.hoverTimer = 0.0F;
        this.cannotEscape = false;
        this.damage = new ArrayList();
        this.bobEffect = new BobEffect();
        this.isPlayer = false;
        this.name = name;
        this.id = id;
        this.maxHealth = maxHealth;
        if (!ignoreBlights && Settings.isEndless && AbstractDungeon.player.hasBlight("ToughEnemies")) {
            float mod = AbstractDungeon.player.getBlight("ToughEnemies").effectFloat();
            this.maxHealth = (int)((float)maxHealth * mod);
        }

        if (ModHelper.isModEnabled("MonsterHunter")) {
            this.currentHealth = (int)((float)this.currentHealth * 1.5F);
        }

        this.currentHealth = this.maxHealth;
        this.currentBlock = 0;
        this.drawX = (float)Settings.WIDTH * 0.75F + offsetX * Settings.scale;
        this.drawY = AbstractDungeon.floorY + offsetY * Settings.scale;
        this.hb_w = hb_w * Settings.scale;
        this.hb_h = hb_h * Settings.scale;
        this.hb_x = hb_x * Settings.scale;
        this.hb_y = hb_y * Settings.scale;
        if (imgUrl != null) {
            this.img = ImageMaster.loadImage(imgUrl);
        }

        this.hb = new Hitbox(this.hb_w, this.hb_h);
        this.healthHb = new Hitbox(this.hb_w, 72.0F * Settings.scale);
        this.refreshHitboxLocation();
        this.showHealthBar();
    }

    public AltAdventurersBase(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl) {
        this(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, 0.0F, 0.0F);
    }

    public void update() {
        Iterator var1 = this.powers.iterator();

        while(var1.hasNext()) {
            AbstractPower p = (AbstractPower)var1.next();
            p.updateParticles();
        }

        this.updateReticle();
        this.updateHealthBar();
        //this.updateAnimations();
        //this.updateDeathAnimation();
        //this.updateEscapeAnimation();
        this.tint.update();
    }

    public void unhover() {
        this.healthHb.hovered = false;
        this.hb.hovered = false;
    }

    public void renderTip(SpriteBatch sb) {
        ArrayList<PowerTip> tips = new ArrayList();

        Iterator var3 = this.powers.iterator();

        while(var3.hasNext()) {
            AbstractPower p = (AbstractPower)var3.next();
            if (p.region48 != null) {
                tips.add(new PowerTip(p.name, p.description, p.region48));
            } else {
                tips.add(new PowerTip(p.name, p.description, p.img));
            }
        }

        if (!tips.isEmpty()) {
            float offsetY = (float)(tips.size() - 1) * MULTI_TIP_Y_OFFSET + TIP_OFFSET_Y;
            if (this.hb.cX + this.hb.width / 2.0F < TIP_X_THRESHOLD) {
                TipHelper.queuePowerTips(this.hb.cX + this.hb.width / 2.0F + TIP_OFFSET_R_X, this.hb.cY + offsetY, tips);
            } else {
                TipHelper.queuePowerTips(this.hb.cX - this.hb.width / 2.0F + TIP_OFFSET_L_X, this.hb.cY + offsetY, tips);
            }
        }

    }

    public void heal(int healAmount) {
        if (!this.isDying) {
            AbstractPower p;
            for(Iterator var2 = this.powers.iterator(); var2.hasNext(); healAmount = p.onHeal(healAmount)) {
                p = (AbstractPower)var2.next();
            }

            this.currentHealth += healAmount;
            if (this.currentHealth > this.maxHealth) {
                this.currentHealth = this.maxHealth;
            }

            if (healAmount > 0) {
                AbstractDungeon.effectList.add(new HealEffect(this.hb.cX - this.animX, this.hb.cY, healAmount));
                this.healthBarUpdatedEvent();
            }

        }
    }

    public void damage(DamageInfo info) {
        if (info.output > 0 && this.hasPower("IntangiblePlayer")) {
            info.output = 1;
        }

        int damageAmount = info.output;
        if (!this.isDying && !this.isEscaping) {
            if (damageAmount < 0) {
                damageAmount = 0;
            }

            boolean hadBlock = true;
            if (this.currentBlock == 0) {
                hadBlock = false;
            }

            boolean weakenedToZero = damageAmount == 0;
            damageAmount = this.decrementBlock(info, damageAmount);
            Iterator var5;
            AbstractRelic r;
            if (info.owner instanceof AbstractPlayer) {
                var5 = AbstractDungeon.player.relics.iterator();

                while(var5.hasNext()) {
                    r = (AbstractRelic)var5.next();
                    r.onAttack(info, damageAmount, this);
                }
            }

            AbstractPower p;
            if (info.owner != null) {
                var5 = info.owner.powers.iterator();

                while(var5.hasNext()) {
                    p = (AbstractPower)var5.next();
                    p.onAttack(info, damageAmount, this);
                }
            }

            for(var5 = this.powers.iterator(); var5.hasNext(); damageAmount = p.onAttacked(info, damageAmount)) {
                p = (AbstractPower)var5.next();
            }

            for(var5 = AbstractDungeon.player.relics.iterator(); var5.hasNext(); damageAmount = r.onAttackedMonster(info, damageAmount)) {
                r = (AbstractRelic)var5.next();
            }

            if (damageAmount > 0) {
                if (info.owner != this) {
                    this.useStaggerAnimation();
                }

                if (damageAmount >= 99 && !CardCrawlGame.overkill) {
                    CardCrawlGame.overkill = true;
                }

                this.currentHealth -= damageAmount;
                AbstractDungeon.effectList.add(new StrikeEffect(this, this.hb.cX, this.hb.cY, damageAmount));
                if (this.currentHealth < 0) {
                    this.currentHealth = 0;
                }

                this.healthBarUpdatedEvent();
            } else if (weakenedToZero && this.currentBlock == 0) {
                if (hadBlock) {
                    AbstractDungeon.effectList.add(new BlockedWordEffect(this, this.hb.cX, this.hb.cY, TEXT[30]));
                } else {
                    AbstractDungeon.effectList.add(new StrikeEffect(this, this.hb.cX, this.hb.cY, 0));
                }
            } else if (Settings.SHOW_DMG_BLOCK) {
                AbstractDungeon.effectList.add(new BlockedWordEffect(this, this.hb.cX, this.hb.cY, TEXT[30]));
            }

            if (this.currentHealth <= 0) {
                this.die();
                if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
                    AbstractDungeon.actionManager.cleanCardQueue();
                    AbstractDungeon.effectList.add(new DeckPoofEffect(64.0F * Settings.scale, 64.0F * Settings.scale, true));
                    AbstractDungeon.effectList.add(new DeckPoofEffect((float)Settings.WIDTH - 64.0F * Settings.scale, 64.0F * Settings.scale, false));
                    AbstractDungeon.overlayMenu.hideCombatPanels();
                }

                if (this.currentBlock > 0) {
                    this.loseBlock();
                    AbstractDungeon.effectList.add(new HbBlockBrokenEffect(this.hb.cX - this.hb.width / 2.0F + BLOCK_ICON_X, this.hb.cY - this.hb.height / 2.0F + BLOCK_ICON_Y));
                }
            }

        }
    }

    public void render(SpriteBatch sb) {
        if (!this.isDead && !this.escaped) {
            if (this.damageFlash) {
                ShaderHelper.setShader(sb, Shader.WHITE_SILHOUETTE);
            }

            if (this.atlas == null) {
                sb.setColor(this.tint.color);
                if (this.img != null) {
                    sb.draw(this.img, this.drawX - (float)this.img.getWidth() * Settings.scale / 2.0F + this.animX, this.drawY + this.animY + AbstractDungeon.sceneOffsetY, (float)this.img.getWidth() * Settings.scale, (float)this.img.getHeight() * Settings.scale, 0, 0, this.img.getWidth(), this.img.getHeight(), this.flipHorizontal, this.flipVertical);
                }
            } else {
                this.state.update(Gdx.graphics.getDeltaTime());
                this.state.apply(this.skeleton);
                this.skeleton.updateWorldTransform();
                this.skeleton.setPosition(this.drawX + this.animX, this.drawY + this.animY + AbstractDungeon.sceneOffsetY);
                this.skeleton.setColor(this.tint.color);
                this.skeleton.setFlip(this.flipHorizontal, this.flipVertical);
                sb.end();
                CardCrawlGame.psb.begin();
                sr.draw(CardCrawlGame.psb, this.skeleton);
                CardCrawlGame.psb.end();
                sb.begin();
                sb.setBlendFunction(770, 771);
            }

//            if (this == AbstractDungeon.getCurrRoom().monsters.hoveredMonster && this.atlas == null) {
//                sb.setBlendFunction(770, 1);
//                sb.setColor(new Color(1.0F, 1.0F, 1.0F, 0.1F));
//                if (this.img != null) {
//                    sb.draw(this.img, this.drawX - (float)this.img.getWidth() * Settings.scale / 2.0F + this.animX, this.drawY + this.animY + AbstractDungeon.sceneOffsetY, (float)this.img.getWidth() * Settings.scale, (float)this.img.getHeight() * Settings.scale, 0, 0, this.img.getWidth(), this.img.getHeight(), this.flipHorizontal, this.flipVertical);
//                    sb.setBlendFunction(770, 771);
//                }
//            }

            if (this.damageFlash) {
                ShaderHelper.setShader(sb, Shader.DEFAULT);
                --this.damageFlashFrames;
                if (this.damageFlashFrames == 0) {
                    this.damageFlash = false;
                }
            }

            this.hb.render(sb);
            this.healthHb.render(sb);
        }

        if (!AbstractDungeon.player.isDead) {
            this.renderHealth(sb);
            this.renderName(sb);
        }

    }

    private void renderName(SpriteBatch sb) {
        if (!this.hb.hovered) {
            this.hoverTimer = MathHelper.fadeLerpSnap(this.hoverTimer, 0.0F);
        } else {
            this.hoverTimer += Gdx.graphics.getDeltaTime();
        }

        if ((!AbstractDungeon.player.isDraggingCard || AbstractDungeon.player.hoveredCard.target == CardTarget.ENEMY) && !this.isDying) {
            Color c = new Color();
            if (this.hoverTimer != 0.0F) {
                if (this.hoverTimer * 2.0F > 1.0F) {
                    c.a = 1.0F;
                } else {
                    c.a = this.hoverTimer * 2.0F;
                }
            } else {
                c.a = MathHelper.slowColorLerpSnap(c.a, 0.0F);
            }

            float tmp = Interpolation.exp5Out.apply(1.5F, 2.0F, this.hoverTimer);
            c.r = Interpolation.fade.apply(Color.DARK_GRAY.r, Settings.CREAM_COLOR.r, this.hoverTimer * 10.0F);
            c.g = Interpolation.fade.apply(Color.DARK_GRAY.g, Settings.CREAM_COLOR.g, this.hoverTimer * 3.0F);
            c.b = Interpolation.fade.apply(Color.DARK_GRAY.b, Settings.CREAM_COLOR.b, this.hoverTimer * 3.0F);
            float y = Interpolation.exp10Out.apply(this.healthHb.cY, this.healthHb.cY - 8.0F * Settings.scale, c.a);
            float x = this.hb.cX - this.animX;
            sb.setColor(new Color(0.0F, 0.0F, 0.0F, c.a / 2.0F * this.hbAlpha));
            AtlasRegion img = ImageMaster.MOVE_NAME_BG;
            sb.draw(img, x - (float)img.packedWidth / 2.0F, y - (float)img.packedHeight / 2.0F, (float)img.packedWidth / 2.0F, (float)img.packedHeight / 2.0F, (float)img.packedWidth, (float)img.packedHeight, Settings.scale * tmp, Settings.scale * 2.0F, 0.0F);
            FontHelper.renderFontCentered(sb, FontHelper.tipHeaderFont, this.name, x, y, new Color(c.r, c.g, c.b, c.a * this.hbAlpha));
        }

    }

    protected void updateHitbox(float hb_x, float hb_y, float hb_w, float hb_h) {
        this.hb_w = hb_w * Settings.scale;
        this.hb_h = hb_h * Settings.scale;
        this.hb_y = hb_y * Settings.scale;
        this.hb_x = hb_x * Settings.scale;
        this.hb = new Hitbox(this.hb_w, this.hb_h);
        this.hb.move(this.drawX + this.hb_x + this.animX, this.drawY + this.hb_y + this.hb_h / 2.0F);
        this.healthHb.move(this.hb.cX, this.hb.cY - this.hb_h / 2.0F - this.healthHb.height / 2.0F);
    }

    private void updateDeathAnimation() {
        if (this.isDying) {
            this.deathTimer -= Gdx.graphics.getDeltaTime();
            if (this.deathTimer < 1.8F && !this.tintFadeOutCalled) {
                this.tintFadeOutCalled = true;
                this.tint.fadeOut();
            }
        }

        if (this.deathTimer < 0.0F) {
            this.isDead = true;
            if (AbstractDungeon.getMonsters().areMonstersDead() && !AbstractDungeon.getCurrRoom().isBattleOver && !AbstractDungeon.getCurrRoom().cannotLose) {
                AbstractDungeon.getCurrRoom().endBattle();
            }

            this.dispose();
            this.powers.clear();
        }

    }

    public void dispose() {
        if (this.atlas != null) {
            this.atlas.dispose();
            logger.info("Disposing Atlas...");
            this.atlas = null;
        }

    }

    private void updateEscapeAnimation() {
        if (this.escapeTimer != 0.0F) {
            this.flipHorizontal = true;
            this.escapeTimer -= Gdx.graphics.getDeltaTime();
            this.drawX += Gdx.graphics.getDeltaTime() * 400.0F * Settings.scale;
        }

        if (this.escapeTimer < 0.0F) {
            this.escaped = true;
            if (AbstractDungeon.getMonsters().areMonstersDead() && !AbstractDungeon.getCurrRoom().isBattleOver && !AbstractDungeon.getCurrRoom().cannotLose) {
                AbstractDungeon.getCurrRoom().endBattle();
            }
        }

    }

    public void escapeNext() {
        this.escapeNext = true;
    }

    public void deathReact() {
    }

    public void escape() {
        this.hideHealthBar();
        this.isEscaping = true;
        this.escapeTimer = 3.0F;
    }

    public void die() {
        this.die(true);
    }

    public void die(boolean triggerRelics) {
        if (!this.isDying) {
            this.isDying = true;
            Iterator var2;
            if (this.currentHealth <= 0 && triggerRelics) {
                var2 = this.powers.iterator();

                while(var2.hasNext()) {
                    AbstractPower p = (AbstractPower)var2.next();
                    p.onDeath();
                }
            }

            if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
                AbstractDungeon.overlayMenu.endTurnButton.disable();
                var2 = AbstractDungeon.player.limbo.group.iterator();

                while(var2.hasNext()) {
                    AbstractCard c = (AbstractCard)var2.next();
                    AbstractDungeon.effectList.add(new ExhaustCardEffect(c));
                }

                AbstractDungeon.player.limbo.clear();
            }

            if (this.currentHealth < 0) {
                this.currentHealth = 0;
            }

            if (!Settings.FAST_MODE) {
                ++this.deathTimer;
            } else {
                ++this.deathTimer;
            }

            StatsScreen.incrementEnemySlain();
        }

    }

    private void calculateDamage(int dmg) {
        AbstractPlayer target = AbstractDungeon.player;
        float tmp = (float)dmg;
        if (Settings.isEndless && AbstractDungeon.player.hasBlight("DeadlyEnemies")) {
            float mod = AbstractDungeon.player.getBlight("DeadlyEnemies").effectFloat();
            tmp *= mod;
        }

        AbstractPower p;
        Iterator var6;
        for(var6 = this.powers.iterator(); var6.hasNext(); tmp = p.atDamageGive(tmp, DamageType.NORMAL)) {
            p = (AbstractPower)var6.next();
        }

        for(var6 = target.powers.iterator(); var6.hasNext(); tmp = p.atDamageReceive(tmp, DamageType.NORMAL)) {
            p = (AbstractPower)var6.next();
        }

        for(var6 = this.powers.iterator(); var6.hasNext(); tmp = p.atDamageFinalGive(tmp, DamageType.NORMAL)) {
            p = (AbstractPower)var6.next();
        }

        for(var6 = target.powers.iterator(); var6.hasNext(); tmp = p.atDamageFinalReceive(tmp, DamageType.NORMAL)) {
            p = (AbstractPower)var6.next();
        }

        dmg = MathUtils.floor(tmp);
        if (dmg < 0) {
            dmg = 0;
        }
    }
}
