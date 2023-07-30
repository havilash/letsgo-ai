package ch.letsGo.ai.model;

public class Fact {
    public String text;

    public Fact() {
    }

    public Fact(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Fact{" +
                "text='" + text + '\'' +
                '}';
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
