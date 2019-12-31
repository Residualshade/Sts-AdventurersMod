package adventurers.cards.uncommon.power;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import adventurers.patches.AbstractCardEnum;

public class FillerUncommonPower extends CustomCard {
    public static final String ID = "adventurers:FillerUncommonPower";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 2;

    public FillerUncommonPower() {
        super(
                ID
                , NAME
                , null
                , COST
                , DESCRIPTION
                , AbstractCard.CardType.POWER
                , AbstractCardEnum.ADVENTURERS_GOLD
                , CardRarity.UNCOMMON
                , CardTarget.SELF
        );
    }

    @Override
    public void upgrade() {
        if(!this.upgraded) {
            this.upgradeName();
        }
    }

    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {
    }
}
