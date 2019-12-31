package adventurers.cards.common.skill;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import adventurers.patches.AbstractCardEnum;

public class Feint extends CustomCard {
    public static final String ID = "adventurers:Feint";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 0;
    private static final int BLOCK = 3;
    private static final int VULNERABLE_TO_APPLY = 1;
    private static final int UPGRADE_BLOCK_BONUS = 1;
    private static final int UPGRADE_VULNERABLE_TO_APPLY = 1;

    public Feint() {
        super(
                ID
                , NAME
                , null
                , COST
                , DESCRIPTION
                , AbstractCard.CardType.SKILL
                , AbstractCardEnum.ADVENTURERS_GOLD
                , CardRarity.COMMON
                , CardTarget.ENEMY
        );

        this.baseBlock = this.block = BLOCK;
        this.baseMagicNumber = this.magicNumber = VULNERABLE_TO_APPLY;
    }

    @Override
    public void upgrade() {
        if(!this.upgraded) {
            this.upgradeName();
            this.upgradeBlock(UPGRADE_BLOCK_BONUS);
            this.upgradeMagicNumber(UPGRADE_VULNERABLE_TO_APPLY);
        }
    }

    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(abstractPlayer,abstractPlayer,this.block));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(abstractMonster,abstractPlayer, new VulnerablePower(abstractMonster,this.magicNumber,false),this.magicNumber));
    }
}
