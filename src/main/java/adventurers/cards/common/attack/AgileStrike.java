package adventurers.cards.common.attack;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;
import adventurers.patches.AbstractCardEnum;

public class AgileStrike extends CustomCard {
    public static final String ID = "adventurers:AgileStrike";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 1;
    private static final int DMG = 3;
    private static final int DEXTERITY_GAIN = 1;
    private static final int UPGRADE_DMG_BONUS = 1;
    private static final int UPGRADE_DEXTERITY_GAIN = 1;

    public AgileStrike() {
        super(
                ID
                , NAME
                , null
                , COST
                , DESCRIPTION
                , CardType.ATTACK
                , AbstractCardEnum.ADVENTURERS_GOLD
                , CardRarity.COMMON
                , CardTarget.ENEMY
        );

        this.baseDamage = this.damage = DMG;
        this.baseMagicNumber = this.magicNumber = DEXTERITY_GAIN;
    }

    @Override
    public void upgrade() {
        if(!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_DMG_BONUS);
            this.upgradeMagicNumber(UPGRADE_DEXTERITY_GAIN);
        }
    }

    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {
        AbstractDungeon.actionManager.addToBottom(new DamageAction(abstractMonster, new DamageInfo(abstractPlayer, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
        if(
            abstractMonster.intent != AbstractMonster.Intent.ATTACK
            && abstractMonster.intent != AbstractMonster.Intent.ATTACK_BUFF
            && abstractMonster.intent != AbstractMonster.Intent.ATTACK_DEBUFF
            && abstractMonster.intent != AbstractMonster.Intent.ATTACK_DEFEND
        ) {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(abstractPlayer,abstractPlayer, new DexterityPower(abstractPlayer,this.magicNumber),this.magicNumber));
        }
    }
}
