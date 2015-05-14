public class Gleitpunktzahl {

	/**
	 * Update by
	 * 
	 * @author Juergen Braeckle (braeckle@in.tum.de)
	 * @author Sebastian Rettenberger (rettenbs@in.tum.de)
	 * @since Oktober 22, 2014
	 * @version 1.2
	 * 
	 *          Diese Klasse beschreibt eine Form von Gleitpunktarithmetik
	 */

	/********************/
	/* Membervariablen: */
	/********************/

	/* Vorzeichen, Mantisse und Exponent der Gleitpunktzahl */
	public boolean vorzeichen; /* true = "-1" */
	public int exponent;
	public int mantisse;

	/*
	 * Anzahl der Bits fuer die Mantisse: einmal gesetzt, soll sie nicht mehr
	 * veraendert werden koennen
	 */
	private static int sizeMantisse = 32;
	private static boolean sizeMantisseFixed = false;

	/*
	 * Anzahl der Bits fuer dem Exponent: einmal gesetzt, soll sie nicht mehr
	 * veraendert werden koennen. Maximale Groesse: 32
	 */
	private static int sizeExponent = 8;
	private static boolean sizeExponentFixed = false;

	/*
	 * Aus der Anzahl an Bits fuer den Exponenten laesst sich der maximale
	 * Exponent und der Offset berechnen
	 */
	private static int maxExponent = (int) Math.pow(2, sizeExponent) - 1;
	private static int expOffset = (int) Math.pow(2, sizeExponent - 1) - 1;

	/**
	 * Falls die Anzahl der Bits der Mantisse noch nicht gesperrt ist, so wird
	 * sie auf abm gesetzt und gesperrt
	 */
	public static void setSizeMantisse(int abm) {
		/*
		 * Falls sizeMantisse noch nicht gesetzt und abm > 0 dann setze auf
		 * abm und sperre den Zugriff
		 */
		if (!sizeMantisseFixed & (abm > 0)) {
			sizeMantisse = abm;
			sizeMantisseFixed = true;
		}
	}

	/**
	 * Falls die Anzahl der Bits des Exponenten noch nicht gesperrt ist, so wird
	 * sie auf abe gesetzt und gesperrt. maxExponent und expOffset werden
	 * festgelegt
	 */
	public static void setSizeExponent(int abe) {
		if (!sizeExponentFixed & (abe > 0)) {
			sizeExponent = abe;
			sizeExponentFixed = true;
			maxExponent = (int) Math.pow(2, abe) - 1;
			expOffset = (int) Math.pow(2, abe - 1) - 1;
		}
	}

	/** Liefert die Anzahl der Bits der Mantisse */
	public static int getSizeMantisse() {
		return sizeMantisse;
	}

	/** Liefert die Anzahl der Bits des Exponenten */
	public static int getSizeExponent() {
		return sizeExponent;
	}

	/**
	 * erzeugt eine Gleitpunktzahl ohne Anfangswert. Die Bitfelder fuer Mantisse
	 * und Exponent werden angelegt. Ist die Anzahl der Bits noch nicht gesetzt,
	 * wird der Standardwert gesperrt
	 */
	Gleitpunktzahl() {
		sizeMantisseFixed = true;
		sizeExponentFixed = true;
	}

	/** erzeugt eine Kopie der reellen Zahl r */
	Gleitpunktzahl(Gleitpunktzahl r) {

		/* Vorzeichen kopieren */
		this.vorzeichen = r.vorzeichen;
		/*
		 * Kopiert den Inhalt der jeweiligen Felder aus r
		 */
		this.exponent = r.exponent;
		this.mantisse = r.mantisse;
	}

	/**
	 * erzeugt eine reelle Zahl mit der Repraesentation des Double-Wertes d. Ist
	 * die Anzahl der Bits fuer Mantisse und Exponent noch nicht gesetzt, wird
	 * der Standardwert gesperrt
	 */
	Gleitpunktzahl(double d) {

		this();
		this.setDouble(d);
	}

	/**
	 * setzt dieses Objekt mit der Repraesentation des Double-Wertes d.
	 */
	public void setDouble(double d) {

		/* Abfangen der Sonderfaelle */
		if (d == 0) {
			this.setNull();
			return;
		}
		if (Double.isInfinite(d)) {
			this.setInfinite(d < 0);
			return;
		}
		if (Double.isNaN(d)) {
			this.setNaN();
			return;
		}

		/* Falls d<0 -> Vorzeichen setzten, Vorzeichen von d wechseln */
		if (d < 0) {
			this.vorzeichen = true;
			d = -d;
		} else
			this.vorzeichen = false;

		/*
		 * Exponent exp von d zur Basis 2 finden d ist danach im Intervall [1,2)
		 */
		int exp = 0;
		while (d >= 2) {
			d = d / 2;
			exp++;
		}
		while (d < 1) {
			d = 2 * d;
			exp--;
		} /* d in [1,2) */
		
		this.exponent = exp + expOffset;

		/*
		 * Mantisse finden; fuer Runden eine Stelle mehr als noetig berechnen
		 */
		double rest = d;
		this.mantisse = 0;
		for (int i = 0; i <= sizeMantisse; i++) {
			this.mantisse <<= 1;
			if (rest >= 1) {
				rest = rest - 1;
				this.mantisse |= 1;
			}
			rest = 2 * rest;
		}
		this.exponent -= 1; /* Mantisse ist um eine Stelle groesser! */

		/*
		 * normalisiere uebernimmt die Aufgaben des Rundens
		 */
		this.normalisiere();
	}

	/** liefert eine String-Repraesentation des Objekts */
	public String toString() {
		if (this.isNaN())
			return "NaN";
		if (this.isNull())
			return "0";
			
		StringBuffer s = new StringBuffer();
		if (this.vorzeichen)
			s.append('-');
		if (this.isInfinite())
			s.append("Inf");
		else {
			for (int i = 32 - Integer.numberOfLeadingZeros(this.mantisse) - 1;
					i >= 0; i--) {
				if (i == sizeMantisse - 2)
					s.append(',');
				if (((this.mantisse >> i) & 1) == 1)
					s.append('1');
				else
					s.append('0');
			}
			//hab hier etwas ver�ndert
			s.append(" * 2^(");
			s.append(this.exponent - expOffset);
			/*s.append("-");
			s.append(expOffset); */
			s.append(")");
		}
		return s.toString();
	}

	/** berechnet den Double-Wert des Objekts */
	public double toDouble() {
		/*
		 * Wenn der Exponent maximal ist, nimmt die Gleitpunktzahl einen der
		 * speziellen Werte an
		 */
		if (this.exponent == maxExponent) {
			/*
			 * Wenn die Mantisse Null ist, hat die Zahl den Wert Unendlich oder
			 * -Unendlich
			 */
			if (this.mantisse == 0) {
				if (this.vorzeichen)
					return -1.0 / 0.0;
				else
					return 1.0 / 0.0;
			}
			/* Ansonsten ist der Wert NaN */
			else
				return 0.0 / 0.0;
		}
		double m = this.mantisse;
		if (this.vorzeichen)
			m *= (-1);
		return m
			* Math.pow(2, (this.exponent - expOffset)
					- (sizeMantisse - 1));
	}

	/**
	 * Sonderfaelle abfragen
	 */
	/** Liefert true, wenn die Gleitpunktzahl die Null repraesentiert */
	public boolean isNull() {
		return (!this.vorzeichen && this.mantisse == 0 && this.exponent == 0);
	}

	/**
	 * Liefert true, wenn die Gleitpunktzahl der NotaNumber Darstellung
	 * entspricht
	 */
	public boolean isNaN() {
		return (this.mantisse != 0 && this.exponent == maxExponent);
	}

	/** Liefert true, wenn die Gleitpunktzahl betragsmaessig unendlich gross ist */
	public boolean isInfinite() {
		return (this.mantisse == 0 && this.exponent == maxExponent);
	}

	/**
	 * vergleicht betragsmaessig den Wert des aktuellen Objekts mit der reellen
	 * Zahl r
	 */
	public int compareAbsTo(Gleitpunktzahl r) {
		/*
		 * liefert groesser gleich 1, falls |this| > |r|
		 * 0, falls |this| = |r|
		 * kleiner gleich -1, falls |this| < |r|
		 */

		/* Exponenten vergleichen */
		int expVergleich = this.exponent - r.exponent;

		if (expVergleich != 0)
			return expVergleich;

		/* Bei gleichen Exponenten: Bitweisses Vergleichen der Mantissen */
		return this.mantisse - r.mantisse;
	}

	/**
	 * normalisiert und rundet das aktuelle Objekt auf die Darstellung r =
	 * (-1)^vorzeichen * 1,r_t-1 r_t-2 ... r_1 r_0 * 2^exponent. Die 0 wird zu
	 * (-1)^0 * 0,00...00 * 2^0 normalisiert WICHTIG: Es kann sein, dass die
	 * Anzahl der Bits nicht mit sizeMantisse uebereinstimmt. Das Ergebnis
	 * soll aber eine Mantisse mit sizeMantisse Bits haben. Deshalb muss
	 * evtl. mit Bits aufgefuellt oder Bits abgeschnitten werden. Dabei muss das
	 * Ergebnis nach Definition gerundet werden.
	 * 
	 * Beispiel: Bei 3 Mantissenbits wird die Zahl 10.11 * 2^-1 zu 1.10 * 2^0
	 */
	public void normalisiere() {
		/*
		 * Beachten Sie, dass die Groesse (Anzahl der Bits) des Exponenten
		 * und der Mantisse durch sizeExponent bzw. sizeMantisse festgelegt
		 * ist.
		 * Achten Sie auf Sonderfaelle!
		 */
		
		
		
		//Sonderfall Null abfangen
		if(mantisse == 0)
		{
			exponent = 0;
			vorzeichen = false;
			return;
		}	
		
		//Errechne Position an der erste 1 stehen sollte
		int highestBit = 1 << (sizeMantisse - 1);
		//Berechne gr��te maximale Mantisse
		int maxMant = (highestBit << 1) - 1;
		
		boolean roundUp = false;//aufrunden oder nicht?
		
		//Ist Mantisse noch kleiner? (Vergleich mit 01000)
		while(mantisse < highestBit)
		{
			mantisse = mantisse << 1;
			
			//check ob unteres Exponenten-Limit
			if(exponent == 0)
			{
				this.setInfinite(true);//-infinity
			}
			else
			{
				this.exponent--;//passe exponent entsprechend an
			}
		}
		
		//Ist Mantisse g��er? (Vergleich mit 01111)
		while(mantisse > maxMant)
		{
			roundUp = (1 & mantisse) == 1;//wir m�ssen nur aufrunden, wenn die abgeschnittene Zahl eine 1 war
			
			mantisse = mantisse >> 1;
			
			//check ob oberes Exponenten-Limit
			if(exponent == maxExponent)
			{
				this.setInfinite(false);//+infinity
			}
			else
			{
				this.exponent++;//passe exponent entsprechend an
			}
		}
		
		//gegebenenfalls aufrunden
		if(roundUp) 
		{
			mantisse += 1;
			
			//Stelle sicher, dass Mantisse durch Aufrunden nicht zu gro�
			if(mantisse > maxMant)
			{
				mantisse = mantisse >> 1;
				//check ob oberes Exponenten-Limit
				if(exponent == maxExponent)
				{
					this.setInfinite(false);//+infinity
				}
				else
				{
					this.exponent++;//passe exponent entsprechend an
				}
			}
			
		}		
		
	}

	/**
	 * denormalisiert die betragsmaessig goessere Zahl, so dass die Exponenten
	 * von a und b gleich sind. Die Mantissen beider Zahlen werden entsprechend
	 * erweitert. Denormalisieren wird fuer add und sub benoetigt.
	 */
	public static void denormalisiere(Gleitpunktzahl a, Gleitpunktzahl b) {
		/*
		 * TODO: hier ist die Operation denormalisiere zu implementieren.
		 */

		//sonderf�lle wof�r wir nicht denormalisieren m�ssen
		if(a == null || b == null || a.isInfinite() || b.isInfinite() || a.isNaN() || b.isNaN() || a.isNull() || b.isNull())
		{
			return;
		}

		//exponenten sind schon gleich
		 if(a.compareAbsTo(b) == 0 || a.exponent == b.exponent)
		 {
			 return;
		 }

		 //suche die betragsm��ig gr��ere zahl
		Gleitpunktzahl greater = a.compareAbsTo(b) > 0 ? a : b;
		Gleitpunktzahl lesser = greater == a ? b : a;
		boolean roundUp = false;

		//verschiebe kleinere zahl bis exponent gleich ist
		while(lesser.exponent < greater.exponent)
		{
			roundUp = (1 & lesser.mantisse) == 1;

			lesser.mantisse = lesser.mantisse >> 1;
			lesser.exponent++;
		}

		//gegebenenfalls aufrunden
		if(roundUp)
		{
			lesser.mantisse += 1;
		}
	}

	/**
	 * addiert das aktuelle Objekt und die Gleitpunktzahl r. Dabei wird zuerst
	 * die betragsmaessig groessere Zahl denormalisiert und die Mantissen beider
	 * zahlen entsprechend vergroessert. Das Ergebnis wird in einem neuen Objekt
	 * gespeichert, normiert, und dieses wird zurueckgegeben.
	 */
	public Gleitpunktzahl add(Gleitpunktzahl r) {
		/*
		 * x: hier ist die Operation add zu implementieren. Verwenden Sie die
		 * Funktionen normalisiere und denormalisiere.
		 * Achten Sie auf Sonderfaelle!
		 */
		
		//Sonderfall NaN und null
		if(r.isNaN() || this.isNaN() || r == null)
		{
			Gleitpunktzahl result = new Gleitpunktzahl();
			result.setNaN();
			return result;
		}
		
		//Sondef�lle f�r Null
		if(r.isNull())
		{
			return new Gleitpunktzahl(this);
		}
		
		if(this.isNull())
		{
			return new Gleitpunktzahl(r);
		}
		
		//Sonderf�lle f�r Infinity
		if(this.isInfinite() && r.isInfinite())
		{
			//unterschiedliche vorzeichen
			if(this.vorzeichen != r.vorzeichen)
			{
				Gleitpunktzahl result = new Gleitpunktzahl();
				result.setNaN();
				return result;
			}
			else//gleiches vorzeichen
			{
				
				return new Gleitpunktzahl(this);
			}
		}
		
		if(this.isInfinite())
		{
			return new Gleitpunktzahl(this);
		}
		
		if(r.isInfinite())
		{
			return new Gleitpunktzahl(r);
		}
		
		//Normaler Fall:		
		Gleitpunktzahl a = new Gleitpunktzahl(this);
		Gleitpunktzahl b = new Gleitpunktzahl(r);
		denormalisiere(a, b);

        //konvertiere zu long
		long aLong = a.vorzeichen ? -a.mantisse : a.mantisse;
        long bLong = b.vorzeichen ? -b.mantisse : b.mantisse;

        //addiere
        long longResult = aLong + bLong;
        Gleitpunktzahl result = new Gleitpunktzahl();

        result.exponent = a.exponent;

        //addiere 2 zu gro�e zahlen
        if(longResult > Integer.MAX_VALUE)
        {
            result.setInfinite(false);
        }
        //addiere 2 zu kleine zahlen
        else if (longResult < Integer.MIN_VALUE)
        {
            result.setInfinite(true);
        }
        else if(longResult == 0)
        {
            result.setNull();
        }
        //normaler fall
        else
        {
            //setze vorzeichen
            result.vorzeichen = longResult < 0;
            //errechne mantisse
            result.mantisse = (int) (longResult < 0 ? -longResult : longResult);
			result.normalisiere();
        }
        return result;
	}

	/**
	 * subtrahiert vom aktuellen Objekt die Gleitpunktzahl r. Dabei wird zuerst
	 * die betragsmaessig groessere Zahl denormalisiert und die Mantissen beider
	 * zahlen entsprechend vergroessert. Das Ergebnis wird in einem neuen Objekt
	 * gespeichert, normiert, und dieses wird zurueckgegeben.
	 */
	public Gleitpunktzahl sub(Gleitpunktzahl r) {
		/*
		 * x: hier ist die Operation sub zu implementieren. Verwenden Sie die
		 * Funktionen normalisiere und denormalisiere.
		 * Achten Sie auf Sonderfaelle!
		 */
		
		//Sonderfall NaN und null
		if(r.isNaN() || this.isNaN() || r == null)
		{
			Gleitpunktzahl result = new Gleitpunktzahl();
			result.setNaN();
			return result;
		}
		
		//Sondef�lle f�r Null
		if(r.isNull())
		{
			return new Gleitpunktzahl(this);
		}
		
		if(this.isNull())
		{
			Gleitpunktzahl result = new Gleitpunktzahl(r);
			result.vorzeichen = !result.vorzeichen;
			return result;
		}
		
		//regul�rer Fall
		Gleitpunktzahl tmp = new Gleitpunktzahl(r);
		tmp.vorzeichen = !tmp.vorzeichen;
		return this.add(tmp);
	}
	
	/**
	 * Setzt die Zahl auf den Sonderfall 0
	 */
	public void setNull() {
		this.vorzeichen = false;
		this.exponent = 0;
		this.mantisse = 0;
	}
	
	/**
	 * Setzt die Zahl auf den Sonderfall +/- unendlich
	 */
	public void setInfinite(boolean vorzeichen) {
		this.vorzeichen = vorzeichen;
		this.exponent = maxExponent;
		this.mantisse = 0;
	}
	
	/**
	 * Setzt die Zahl auf den Sonderfall NaN
	 */
	public void setNaN() {
		this.vorzeichen = false;
		this.exponent = maxExponent;
		this.mantisse = 1;
	}
}
