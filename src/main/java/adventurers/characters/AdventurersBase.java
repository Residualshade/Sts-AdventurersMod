package adventurers.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.LizardTail;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.BlockedWordEffect;
import com.megacrit.cardcrawl.vfx.combat.HbBlockBrokenEffect;
import com.megacrit.cardcrawl.vfx.combat.StrikeEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;

import static com.megacrit.cardcrawl.helpers.ShaderHelper.Shader.DEFAULT;
import static com.megacrit.cardcrawl.helpers.ShaderHelper.Shader.WHITE_SILHOUETTE;
import static com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase.COMBAT;

public class AdventurersBase extends AbstractCreature {

    private static final Logger logger = LogManager.getLogger(AdventurersBase.class.getName());

    public Texture img;

    public boolean renderCorpse = false;
    private int damagedThisCombat;

    public AbstractPlayer parentPlayer;


    public AdventurersBase(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        this.isDead = false;
        this.isPlayer = false;
        this.name = name;
        this.id = id;
        this.maxHealth = maxHealth;
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

    @Override
    public void damage(DamageInfo damageInfo) {
        int damageAmount = damageInfo.output;
        boolean hadBlock = true;
        if (this.currentBlock == 0) {
            hadBlock = false;
        }

        if (damageAmount < 0) {
            damageAmount = 0;
            logger.info("LINE 726: AbstractPlayer.java: Why is damageAmount less than 0?");
        }

        if (damageAmount > 1 && this.hasPower("IntangiblePlayer")) {
            damageAmount = 1;
        }

        damageAmount = this.decrementBlock(damageInfo, damageAmount);
        Iterator var4;
        AbstractRelic r;
        if (damageInfo.owner == this) {
            var4 = this.getRelics().iterator();

            while(var4.hasNext()) {
                r = (AbstractRelic)var4.next();
                r.onAttack(damageInfo, damageAmount, this);
            }
        }

        AbstractPower p;
        if (damageInfo.owner != null) {
            var4 = damageInfo.owner.powers.iterator();

            while(var4.hasNext()) {
                p = (AbstractPower)var4.next();
                p.onAttack(damageInfo, damageAmount, this);
            }

            for(var4 = this.powers.iterator(); var4.hasNext(); damageAmount = p.onAttacked(damageInfo, damageAmount)) {
                p = (AbstractPower)var4.next();
            }

            for(var4 = this.getRelics().iterator(); var4.hasNext(); damageAmount = r.onAttacked(damageInfo, damageAmount)) {
                r = (AbstractRelic)var4.next();
            }
        } else {
            logger.info("NO OWNER, DON'T TRIGGER POWERS");
        }

        if (damageAmount > 0) {
            for(var4 = this.powers.iterator(); var4.hasNext(); damageAmount = p.onLoseHp(damageAmount)) {
                p = (AbstractPower)var4.next();
            }

            var4 = this.getRelics().iterator();

            while(var4.hasNext()) {
                r = (AbstractRelic)var4.next();
                r.onLoseHp(damageAmount);
            }

            if (damageInfo.owner != null) {
                var4 = damageInfo.owner.powers.iterator();

                while(var4.hasNext()) {
                    p = (AbstractPower)var4.next();
                    p.onInflictDamage(damageInfo, damageAmount, this);
                }
            }

            if (damageInfo.owner != this) {
                this.useStaggerAnimation();
            }

            if (damageInfo.type == DamageInfo.DamageType.HP_LOSS) {
                GameActionManager.hpLossThisCombat += damageAmount;
            }

            GameActionManager.damageReceivedThisTurn += damageAmount;
            GameActionManager.damageReceivedThisCombat += damageAmount;
            this.currentHealth -= damageAmount;
            if (damageAmount > 0 && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
                this.updateCardsOnDamage();
                ++this.damagedThisCombat;
            }

            AbstractDungeon.effectList.add(new StrikeEffect(this, this.hb.cX, this.hb.cY, damageAmount));
            if (this.currentHealth < 0) {
                this.currentHealth = 0;
            } else if (this.currentHealth < this.maxHealth / 4) {
                AbstractDungeon.topLevelEffects.add(new BorderFlashEffect(new Color(1.0F, 0.1F, 0.05F, 0.0F)));
            }

            this.healthBarUpdatedEvent();
            if ((float)this.currentHealth <= (float)this.maxHealth / 2.0F && !this.isBloodied) {
                this.isBloodied = true;
                var4 = this.getRelics().iterator();

                while(var4.hasNext()) {
                    r = (AbstractRelic)var4.next();
                    if (r != null) {
                        r.onBloodied();
                    }
                }
            }

            if (this.currentHealth < 1) {
                if (!this.hasRelic("Mark of the Bloom")) {
                    if (this.hasPotion("FairyPotion")) {
                        var4 = this.getPotions().iterator();

                        while(var4.hasNext()) {
                            AbstractPotion potion = (AbstractPotion)var4.next();
                            if (potion.ID.equals("FairyPotion")) {
                                potion.flash();
                                this.currentHealth = 0;
                                potion.use(this);
                                AbstractDungeon.topPanel.destroyPotion(potion.slot);
                                return;
                            }
                        }
                    } else if (this.hasRelic("Lizard Tail") && ((LizardTail)this.getRelic("Lizard Tail")).counter == -1) {
                        this.currentHealth = 0;
                        this.getRelic("Lizard Tail").onTrigger();
                        return;
                    }
                }

                this.isDead = true;
                AbstractDungeon.deathScreen = new DeathScreen(AbstractDungeon.getMonsters());
                this.currentHealth = 0;
                if (this.currentBlock > 0) {
                    this.loseBlock();
                    AbstractDungeon.effectList.add(new HbBlockBrokenEffect(this.hb.cX - this.hb.width / 2.0F + BLOCK_ICON_X, this.hb.cY - this.hb.height / 2.0F + BLOCK_ICON_Y));
                }
            }
        } else if (this.currentBlock > 0) {
            AbstractDungeon.effectList.add(new BlockedWordEffect(this, this.hb.cX, this.hb.cY, "N/A" /*BLOCKED_STRING*/));
        } else if (!hadBlock) {
            AbstractDungeon.effectList.add(new StrikeEffect(this, this.hb.cX, this.hb.cY, 0));
        }
    }

    public ArrayList<AbstractRelic> getRelics() {
        return parentPlayer.relics;
    }

    public ArrayList<AbstractPotion> getPotions() {
        return parentPlayer.potions;
    }

    private boolean hasPotion(String id) {
        Iterator var2 = this.getPotions().iterator();

        AbstractPotion p;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            p = (AbstractPotion)var2.next();
        } while(!p.ID.equals(id));

        return true;
    }

    private void updateCardsOnDamage() {
    }

    private boolean hasRelic(String id) {
        Iterator var2 = this.getRelics().iterator();

        AbstractRelic r;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            r = (AbstractRelic)var2.next();
        } while(!r.relicId.equals(id));

        return true;
    }

    private AbstractRelic getRelic(String id) {
        Iterator var2 = this.getRelics().iterator();

        AbstractRelic r;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            r = (AbstractRelic)var2.next();
        } while(!r.relicId.equals(id));

        return r;
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        if ((AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT || AbstractDungeon.getCurrRoom() instanceof MonsterRoom) && !this.isDead) {
            this.myRenderHealth(spriteBatch);
        }

        if (!(AbstractDungeon.getCurrRoom() instanceof RestRoom)) {
            if (this.damageFlash) {
                ShaderHelper.setShader(spriteBatch, WHITE_SILHOUETTE);
            }

            if (this.atlas != null && !this.renderCorpse) {
                this.renderAdventurer(spriteBatch);
            } else {
                spriteBatch.setColor(Color.WHITE);
                spriteBatch.draw(this.img, this.drawX - (float)this.img.getWidth() * Settings.scale / 2.0F + this.animX, this.drawY, (float)this.img.getWidth() * Settings.scale, (float)this.img.getHeight() * Settings.scale, 0, 0, this.img.getWidth(), this.img.getHeight(), this.flipHorizontal, this.flipVertical);
            }

            if (this.damageFlash) {
                ShaderHelper.setShader(spriteBatch, DEFAULT);
                --this.damageFlashFrames;
                if (this.damageFlashFrames == 0) {
                    this.damageFlash = false;
                }
            }

            this.hb.render(spriteBatch);
            this.healthHb.render(spriteBatch);
            this.renderName(spriteBatch);
        }
    }

    public void myRenderHealth(SpriteBatch spriteBatch) {

    }

    private void renderAdventurer(SpriteBatch spriteBatch) {
        if (this.atlas != null) {
            this.state.update(Gdx.graphics.getDeltaTime());
            this.state.apply(this.skeleton);
            this.skeleton.updateWorldTransform();
            this.skeleton.setPosition(this.drawX + this.animX, this.drawY + this.animY + AbstractDungeon.sceneOffsetY);
            this.skeleton.setColor(this.tint.color);
            this.skeleton.setFlip(this.flipHorizontal, this.flipVertical);
            spriteBatch.end();
            CardCrawlGame.psb.begin();
            sr.draw(CardCrawlGame.psb, this.skeleton);
            CardCrawlGame.psb.end();
            spriteBatch.begin();
        } else {
            spriteBatch.setColor(Color.WHITE);
            spriteBatch.draw(this.img, this.drawX - (float)this.img.getWidth() * Settings.scale / 2.0F + this.animX, this.drawY, (float)this.img.getWidth() * Settings.scale, (float)this.img.getHeight() * Settings.scale, 0, 0, this.img.getWidth(), this.img.getHeight(), this.flipHorizontal, this.flipVertical);
        }
    }

    private void renderName(SpriteBatch spriteBatch) {
        Color c = new Color();
        c.r = Interpolation.fade.apply(Color.DARK_GRAY.r, Settings.CREAM_COLOR.r,1);
        c.g = Interpolation.fade.apply(Color.DARK_GRAY.g, Settings.CREAM_COLOR.g, 1);
        c.b = Interpolation.fade.apply(Color.DARK_GRAY.b, Settings.CREAM_COLOR.b, 1);
        float y = Interpolation.exp10Out.apply(this.healthHb.cY, this.healthHb.cY - 8.0F * Settings.scale, c.a);
        float x = this.hb.cX - this.animX;
        FontHelper.renderFontCentered(spriteBatch, FontHelper.tipHeaderFont, this.name, x, y, new Color(c.r, c.g, c.b, c.a * this.hbAlpha));
    }

    public void update() {
        this.hb.update();
        this.updateHealthBar();
        this.updatePowers();
        this.healthHb.update();
        this.updateReticle();
        this.tint.update();
    }
}
