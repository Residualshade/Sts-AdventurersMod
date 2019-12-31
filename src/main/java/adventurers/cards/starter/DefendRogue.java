package adventurers.cards.starter;

import adventurers.patches.AbstractCardEnum;
import basemod.abstracts.CustomCard;
import basemod.helpers.BaseModCardTags;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class DefendRogue extends CustomCard {

    public static final String ID = "adventurers:DefendRogue";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 1;
    private static final int BLOCK = 5;
    private static final int UPGRADE_BLOCK_BONUS = 3;

    //TODO: CARD IMAGE
    public DefendRogue() {
        super(
            ID
            ,NAME
            ,null
            ,COST
            ,DESCRIPTION
            , CardType.SKILL
            , AbstractCardEnum.ADVENTURERS_GOLD
            , CardRarity.BASIC
            , CardTarget.SELF
        );

        this.baseBlock = this.block = BLOCK;

        this.tags.add(BaseModCardTags.BASIC_DEFEND);
    }

    @Override
    public void upgrade() {
        if(!this.upgraded) {
            this.upgradeName();
            this.upgradeBlock(UPGRADE_BLOCK_BONUS);
        }
    }

    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(abstractPlayer,abstractPlayer,this.block));
    }
}
