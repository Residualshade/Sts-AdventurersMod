package adventurers.cards.common.skill;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import adventurers.patches.AbstractCardEnum;

public class StoicGuard extends CustomCard {
    public static final String ID = "adventurers:StoicGuard";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 1;
    private static final int BLOCK = 3;
    private static final int STRENGTH_GAIN = 1;
    private static final int UPGRADE_BLOCK_BONUS = 1;
    private static final int UPGRADE_STRENGTH_GAIN = 1;

    public StoicGuard() {
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
        this.baseMagicNumber = this.magicNumber = STRENGTH_GAIN;
    }

    @Override
    public void upgrade() {
        if(!this.upgraded) {
            this.upgradeName();
            this.upgradeBlock(UPGRADE_BLOCK_BONUS);
            this.upgradeMagicNumber(UPGRADE_STRENGTH_GAIN);
        }
    }

    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(abstractPlayer,abstractPlayer,this.block));
        if(
            abstractMonster.intent == AbstractMonster.Intent.ATTACK
            || abstractMonster.intent == AbstractMonster.Intent.ATTACK_BUFF
            || abstractMonster.intent == AbstractMonster.Intent.ATTACK_DEBUFF
            || abstractMonster.intent == AbstractMonster.Intent.ATTACK_DEFEND
        ) {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(abstractPlayer,abstractPlayer, new StrengthPower(abstractPlayer,this.magicNumber),this.magicNumber));
        }
    }
}
