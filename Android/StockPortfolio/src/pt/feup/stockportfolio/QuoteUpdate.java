package pt.feup.stockportfolio;

public class QuoteUpdate extends Quote {


	private static final long serialVersionUID = 1786316402894324819L;
	double change = 0;
	
	QuoteUpdate(Quote q)
	{
		super(q);
		change = 0;
	}
	
	QuoteUpdate(Quote q, double c)
	{
		super(q);
		change = c;
	}


}
