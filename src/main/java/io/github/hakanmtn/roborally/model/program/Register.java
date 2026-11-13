package io.github.hakanmtn.roborally.model.program;

public class Register {

    private CardType card;

    public boolean isEmpty() {
        return card == null;
    }

    public void setCard(CardType card) {
        this.card = card;
    }
    public CardType getCard() {
        return card;
    }
}
