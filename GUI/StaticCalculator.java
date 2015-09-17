
/**
 * Class StaticCalculator - used to do calculations.

 * @author Andrew Scott
 * @version March 2014
 */
public class StaticCalculator{

    /**
     * Return the add result for this Calculator
     * @return add
     */
    public static int add(int first, int second) {
        return first + second;
    }

    /**
     * Return the subtract result for this Calculator
     * @return subtract
     */
    public static int subtract(int first, int second) {
        return first - second;
    }

    /**
     * Return the multiply result for this Calculator
     * @return multiply
     */
    public static int multiply(int first, int second) {
        return first * second;
    }

    /**
     * Return the divide result for this Calculator
     * @return divide
     */
    public static int divide(int first, int second) {
        return first / second;
    }

   /**
     * Return the modulus divide result for this Calculator
     * @return modulus divide
     */
    public static int mod(int first, int second) {
        return first % second;
    }

   /**
     * Return the maximum result for this Calculator
     * @return maximum
     */
    public static int max(int first, int second) {
    
        if (first > second)
            return first ;
        else
            return second;
    }//end max

  /**
     * Return the minimum result for this Calculator
     * @return minimum 
     */
    public static int min(int first, int second) {
          if (first < second)
            return first ;
        else
            return second;
    }//end min

 public static void main(String[] argv) {
    System.out.println(add(100,20));
    System.out.println(subtract(100,20));
    System.out.println(multiply(100,20));
    System.out.println(divide(100,20));
    System.out.println(mod(101,20));
    System.out.println(max(101,20));
    System.out.println(min(101,20));
    }//main
}//class
