

public class Gauss {

	/**
	 * Diese Methode soll die Loesung x des LGS R*x=b durch
	 * Rueckwaertssubstitution ermitteln.
	 * PARAMETER: 
	 * R: Eine obere Dreiecksmatrix der Groesse n x n 
	 * b: Ein Vektor der Laenge n
	 */
	public static double[] backSubst(double[][] R, double[] b) {
		
		//copy R and b
		double[][] matrix = R.clone();
		double[] vector = b.clone();
		
		//make sure the diagonal has 1s only
		for(int i = 0; i < matrix.length; i++)
		{
			double diagonalValue = matrix[i][i];
			
			//divide every value in a row
			for(int j = 0; j < matrix.length; j++)
			{
				matrix[i][j] /= diagonalValue;
			}
			
			//also divide vector entry i
			b[i] /= diagonalValue;
		}
		
		//make sure the upper triangle consists of 0s only
		for(int i = matrix.length - 1; i > 0; i--)
		{
			//for every row above
			for(int j = i - 1; j >= 0; j--)
			{
				double valueToEliminate = matrix[j][i];
				
				//only change vector values since matrix is not returned/changed
				vector[j] -= valueToEliminate * vector[i];
			}
			
		}		
		
		return vector;
	}

	/**
	 * Diese Methode soll die Loesung x des LGS A*x=b durch Gauss-Elimination mit
	 * Spaltenpivotisierung ermitteln. A und b sollen dabei nicht veraendert werden. 
	 * PARAMETER: A:
	 * Eine regulaere Matrix der Groesse n x n 
	 * b: Ein Vektor der Laenge n
	 */
	public static double[] solve(double[][] A, double[] b) {
		
		//clone A and b
		double[][] matrix = A.clone();
		double[] vector = b.clone();
		
		for(int i = 0; i < matrix.length - 1; i++)
		{
			double max = 0;
			int indexMax = i;
			
			//Spaltenpivotisierung
			for(int j = i; j < matrix.length; j++)
			{
				//find maximum
				if(Math.abs(matrix[j][i]) > max)
				{
					max = matrix[j][i];
					indexMax = j;
				}
			}
			
			//swap rows
			swapRow(matrix, vector, i, indexMax);
			
			double diagonalValue = matrix[i][i];
			
			//divide row i by diagonal element
			for(int j = 0; j < matrix.length; j++)
			{
				matrix[i][j] /= diagonalValue;
			}
			
			//also divide vector entry i
			b[i] /= diagonalValue;
			
			
			//for every row below i
			for(int j = i + 1; j < matrix.length; j++)
			{
				
				double factor = matrix[j][i];
				
				//subtract row i * entry of row j
				for(int k = i; k < matrix.length; k++)
				{
					matrix[j][k] -= factor * matrix[i][k];
				}
				
				//do the same with the vector
				vector[j] -= vector[i] * factor;
			}
		}
		
		
		return backSubst(matrix, vector);	
	}

	/**
	 * Diese Methode soll eine Loesung p!=0 des LGS A*p=0 ermitteln. A ist dabei
	 * eine nicht invertierbare Matrix. A soll dabei nicht veraendert werden.
	 * 
	 * Gehen Sie dazu folgendermassen vor (vgl.Aufgabenblatt): 
	 * -Fuehren Sie zunaechst den Gauss-Algorithmus mit Spaltenpivotisierung 
	 *  solange durch, bis in einem Schritt alle moeglichen Pivotelemente
	 *  numerisch gleich 0 sind (d.h. <1E-10) 
	 * -Betrachten Sie die bis jetzt entstandene obere Dreiecksmatrix T und
	 *  loesen Sie Tx = -v durch Rueckwaertssubstitution 
	 * -Geben Sie den Vektor (x,1,0,...,0) zurueck
	 * 
	 * Sollte A doch intvertierbar sein, kann immer ein Pivot-Element gefunden werden(>=1E-10).
	 * In diesem Fall soll der 0-Vektor zurueckgegeben werden. 
	 * PARAMETER: 
	 * A: Eine singulaere Matrix der Groesse n x n 
	 */
	public static double[] solveSing(double[][] A) {
		//clone A
		double[][] matrix = A.clone();
				
		for(int i = 0; i < matrix.length - 1; i++)
		{
			double max = 0;
			int indexMax = i;
			
			//Spaltenpivotisierung
			for(int j = i; j < matrix.length; j++)
			{
				//find maximum
				if(Math.abs(matrix[j][i]) > max)
				{
					max = matrix[j][i];
					indexMax = j;
				}
			}
			
			//if no suitable pivot element can be found
			if(Math.abs(max) < Math.pow(10, -10))
			{
				//downsize matrix and create new vector
				double[][] downsizedMatrix = downsizeMatrix(matrix, i - 1);
				double[] newVector = new double[downsizedMatrix.length];
				
				for(int j = 0; j < newVector.length; j++)
				{
					//negative entry of matrix
					newVector[j] = -matrix[i][j];
				}
				
				double[] x = backSubst(downsizedMatrix, newVector);
				
				//fill result vector with (x, 1, 0, 0, ...)
				double[] result = new double[matrix.length];
				
				for(int j = 0; j < result.length; j++)
				{
					if(j < x.length)
					{
						result[j] = x[j];
					}
					else if(j == x.length)
					{
						result[j] = 1;
					}
					else
					{
						result[j] = 0;
					}
				}
				
				return result;
			}
			
			//swap rows
			swapRow(matrix, new double[matrix.length], i, indexMax);
			
			double diagonalValue = matrix[i][i];
			
			//divide row i by diagonal element
			for(int j = 0; j < matrix.length; j++)
			{
				matrix[i][j] /= diagonalValue;
			}
			
			
			
			//for every row below i
			for(int j = i + 1; j < matrix.length; j++)
			{
				
				double factor = matrix[j][i];
				
				//subtract row i * entry of row j
				for(int k = i; k < matrix.length; k++)
				{
					matrix[j][k] -= factor * matrix[i][k];
				}
			}
		}
		
		
		return new double[matrix.length];//return zero vector
	}

	/**
	 * Diese Methode berechnet das Matrix-Vektor-Produkt A*x mit A einer nxm
	 * Matrix und x einem Vektor der Laenge m. Sie eignet sich zum Testen der
	 * Gauss-Loesung
	 */
	public static double[] matrixVectorMult(double[][] A, double[] x) {
		int n = A.length;
		int m = x.length;

		double[] y = new double[n];

		for (int i = 0; i < n; i++) {
			y[i] = 0;
			for (int j = 0; j < m; j++) {
				y[i] += A[i][j] * x[j];
			}
		}

		return y;
	}
	
	private static void swapRow(double[][] matrix, double[] vector, int row1, int row2)
	{
		//swap matrix
		for(int i = 0; i < matrix.length; i++)
		{
			double temp = matrix[row1][i];
			matrix[row1][i] = matrix[row2][i];
			matrix[row2][i] = temp;
		}
		
		//swap vector 
		double temp = vector[row1];
		vector[row1] = vector[row2];
		vector[row2] = temp;
	}
	
	private static double[][] downsizeMatrix(double[][] matrix, int rowToDownsizeTo)
	{
		double[][] downsized = new double[rowToDownsizeTo][rowToDownsizeTo];
		
		for(int i = 0; i < rowToDownsizeTo; i++)
		{
			for(int j = 0; j < rowToDownsizeTo; j++)
			{
				downsized[i][j] = matrix[i][j];
			}
		}
		
		return downsized;
	}
}
