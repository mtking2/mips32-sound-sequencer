  import java.awt.*;
  import java.awt.event.*;
  import javax.swing.*;
  import javax.swing.text.*;
  

/**
 * Represents a  GUI interface four our calculator.
 * 
 * @author Andrew Scott
 * @version V1.0 Spring 2014
 */

public class GuiCalculator extends JFrame implements ItemListener {

        /**The panel of the JFrame**/
        JPanel contentPane;

        /**The panel to hold the content.**/
        JPanel jPanel1 = new JPanel();

        /**The result**/
        int result;

        /**The label for the first value**/
        JLabel firstLabel;

        /**The label for the second value**/
        JLabel secondLabel;

        /**The label to display the result.**/
        JLabel resultLabel = new JLabel();

        /**The text field for the first value**/
        JTextField firstText;

        /**The text field for the second value**/
        JTextField secondText;

        /**The text field to hold the result.**/
        JTextField resultText;

        /**A drop down choice**/
        Choice calcChoice;

    //=================================================================================================================
    /**
     * Constructor for objects of class GuiCalculator
     */
    //==================================================================================================================
    public GuiCalculator()
    {
        // initialise instance variables
          this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         jPanel1 = (JPanel)getContentPane();
          jPanel1.setLayout(new FlowLayout());
          this.setTitle("Gui Cacluator");

         setUpValue1();
         setUpValue2();
         setUpResults();
        // setUpResults();
         setupOptions();

        jPanel1.setLayout(new FlowLayout());
       // contentPane.add(jPanel1);

        jPanel1.setLayout(new FlowLayout());

    }//constructor======================================================================================================

    //==================================================================================================================
    /**
     * Set up a JLabel and JText field for the first value input field.
     */
    //==================================================================================================================
    public void setUpValue1(){
        firstLabel = new JLabel();
        firstText =  new JTextField(10);
        firstLabel.setText("First value");
        jPanel1.add(firstLabel);
        jPanel1.add(firstText);
    }//=================================================================================================================

    //==================================================================================================================
    /**
     * Set up a JLabel and JText field for the first value input field.
     */
    //==================================================================================================================
    public void setUpValue2(){
        secondLabel = new JLabel();
        secondText = new JTextField(10);
        secondLabel.setText("Second value");
        jPanel1.add(secondLabel);
        jPanel1.add(secondText);
    }//=================================================================================================================

    //==================================================================================================================
    /**
     * Set up a JLabel and JText field for the second value input field.
     */
    //==================================================================================================================
    public void setUpResults(){

        resultLabel.setText("Result");
        jPanel1.add(resultLabel);

        resultText = new JTextField(10);
        resultText.setEditable(false);
        jPanel1.add(resultText);
    }//=================================================================================================================



    //==================================================================================================================
    /**
     * Set the list of options
     */
    //==================================================================================================================
    public void setupOptions(){
        calcChoice = new Choice();
        calcChoice.addItem("Add");
        calcChoice.addItem("Subtract");
        calcChoice.addItem("Multiply");
        calcChoice.addItem("Integer Divide");
        calcChoice.addItem("Modulus Divide");
        calcChoice.addItem("Maximum");
        calcChoice.addItem("Minimum");
        // calcChoice.addItem("Exit");
        jPanel1.add(calcChoice);
        calcChoice.addItemListener(this);
    }//=================================================================================================================


    //==================================================================================================================
    /** Executes the newly selected Choice item.
    * Requests the Count c to perform appropriate method.
    * Called when user generates an ItemEvent by selecting from this Choice
    * @param e ItemEvent generated by CountGuiChoice.
    *///===============================================================================================================
    public void itemStateChanged(ItemEvent e){

        int firstValue = 0;
        int secondValue = 0;
        try {
           firstValue = Integer.parseInt(firstText.getText().trim());
           secondValue = Integer.parseInt(secondText.getText().trim());

        }
        catch(NumberFormatException nfe){
            firstValue = 0;
            secondValue= 0;
        }

        switch (calcChoice.getSelectedIndex()) {
          case 0 :result = StaticCalculator.add(firstValue,secondValue);
                  break;
          case 1 :result = StaticCalculator.subtract(firstValue,secondValue);
                  break;
          case 2 :result = StaticCalculator.multiply(firstValue,secondValue);
                  break;
          case 3 :result = StaticCalculator.divide(firstValue,secondValue);
                  break;
          case 4 :result = StaticCalculator.mod(firstValue,secondValue);
                  break;
          case 5 :result = StaticCalculator.max(firstValue,secondValue);
                  break;
          case 6 :result = StaticCalculator.min(firstValue,secondValue);
                  break;
         }//switch

    resultText.setText(""+result) ;
    }//itemStateChanged=================================================================================================


    //==================================================================================================================
    /**
     * Where it all begins
     * @param args
     */
    //==================================================================================================================
    public static void main(String args[]) {
      GuiCalculator frame = new GuiCalculator();
      frame.pack();
      frame.setVisible(true);
    }//main=============================================================================================================
}//class################################################################################################################
 
