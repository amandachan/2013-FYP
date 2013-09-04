package attacks;

public abstract class Attack {
	public abstract void giveUnfairRating(int id);
	public abstract int chooseSeller();
	public abstract int chooseProduct(int id);
	public abstract double buyProduct(int id);
}
