package adventurers.cards.common.skill;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;
import adventurers.patches.AbstractCardEnum;

public class WeakFinisher extends CustomCard {
    public static final String ID = "adventurers:WeakFinisher";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 2;
    private static final int BLOCK = 9;
    private static final int CONDITIONAL_BLOCK_BONUS = 3;
    private static final int UPGRADE_BLOCK_BONUS = 3;
    private static final int UPGRADE_CONDITIONAL_BLOCK_BONUS = 2;

    public WeakFinisher() {
        super(
                ID
                , NAME
                , null
                , COST
                , DESCRIPTION
                , CardType.SKILL
                , AbstractCardEnum.ADVENTURERS_GOLD
                , CardRarity.COMMON
                , CardTarget.ENEMY
        );

        this.baseBlock = this.block = BLOCK;
        this.baseMagicNumber = this.magicNumber = CONDITIONAL_BLOCK_BONUS;
    }

    @Override
    public void upgrade() {
        if(!this.upgraded) {
            this.upgradeName();
            this.upgradeBlock(UPGRADE_BLOCK_BONUS);
            this.upgradeMagicNumber(UPGRADE_CONDITIONAL_BLOCK_BONUS);
        }
    }

    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {
        if(abstractMonster.hasPower(WeakPower.POWER_ID)) {
            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(abstractPlayer,abstractPlayer,this.block+this.magicNumber));
        } else {
            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(abstractPlayer,abstractPlayer,this.block));
        }
    }
}
