package adventurers.cards.common.attack;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;
import adventurers.patches.AbstractCardEnum;

public class Maim extends CustomCard {
    public static final String ID = "adventurers:Maim";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 0;
    private static final int DMG = 3;
    private static final int WEAK_TO_APPLY = 1;
    private static final int UPGRADE_DMG_BONUS = 1;
    private static final int UPGRADE_WEAK_TO_APPLY = 1;

    public Maim() {
        super(
                ID
                , NAME
                , null
                , COST
                , DESCRIPTION
                , AbstractCard.CardType.ATTACK
                , AbstractCardEnum.ADVENTURERS_GOLD
                , CardRarity.COMMON
                , CardTarget.ENEMY
        );

        this.baseDamage = this.damage = DMG;
        this.baseMagicNumber = this.magicNumber = WEAK_TO_APPLY;
    }

    @Override
    public void upgrade() {
        if(!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_DMG_BONUS);
            this.upgradeMagicNumber(UPGRADE_WEAK_TO_APPLY);
        }
    }

    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {
        AbstractDungeon.actionManager.addToBottom(new DamageAction(abstractMonster, new DamageInfo(abstractPlayer, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(abstractMonster,abstractPlayer, new WeakPower(abstractMonster,this.magicNumber,false),this.magicNumber));
    }
}
