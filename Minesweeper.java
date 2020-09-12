import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Minesweeper extends JFrame {

   private JButton[][] buttons = new JButton[16][30];
   
   private int [][] revealed = new int[16][30];  // 0 = unrevealed, -1 = marked, 1 = revealed. this keeps track of the buttons that have had their value revealed
   private static int [][] values = new int[16][30]; // 0 = nothing, <0 = bomb, >0 = number of bombs in vicinity. this will be one of the first things created (get it working fully first then modify it to make first move safe)
   private boolean firstMove = true;
   private JLabel label; 
   private JButton reset = new JButton("Reset");
   private int flag = 99;
   private int spaces = 381;

   public static void main(String[] args) {
      int rows = 16;
      int cols = 30;
      Minesweeper gt = new Minesweeper(rows, cols);
      gt.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      gt.pack();
      gt.setVisible(true);
      
   }

   public Minesweeper(int rows, int cols) {
	  JPanel north = new JPanel(new FlowLayout());
      north.add(reset);
      reset.setEnabled(false);
      add(north, BorderLayout.NORTH);
      reset.addActionListener(new Class3());
	  JPanel center = new JPanel(new GridLayout(rows, cols));
      center.setLayout(new GridLayout(rows, cols));
      add(center, BorderLayout.CENTER);
      JPanel south = new JPanel(new FlowLayout());
      label = new JLabel("Welcome to Minesweeper!");
      south.add(label);
      add(south, BorderLayout.SOUTH);

      
   
      for(int r = 0; r < 16; r++){
         for(int c = 0; c < 30; c++){
            JButton button = new JButton();    //creates a JButton with no label and places it in the GUI after adding to the list of buttons
            button.setPreferredSize(new Dimension(60, 30));
            buttons[r][c] = button;
            center.add(buttons[r][c]);
         }
      }
      
      for(int i = 0; i < 16; i++){
         for(int j = 0; j < 30; j++){
            buttons[i][j].addActionListener(new Class1(i, j));
            buttons[i][j].addMouseListener(new Class2(i, j));
         }
      }
   }
   
   public void setBombs(int row, int col){   //iterates through the list until it runs out of bombs randoming placing them with a 10% chance each time
      int bombs = 99;
      while(bombs > 0){
         if(row == 0 && col == 0){
            for(int r = 0; r < values.length; r++)
               for(int c = 0; c < values[r].length; c++)
                  if((r>row+1 || c>col+1) && values[r][c] >= 0 && (int)(Math.random()*10) == 1){
                     values[r][c] = -1;
                     bombs--;
                     if(bombs == 0)
                        return;
                  }
         }else if(row == 0){
            for(int r = 0; r < values.length; r++)
               for(int c = 0; c < values[r].length; c++)
                  if((r>row+1 || c<col-1 || c>col+1) && values[r][c] >= 0 && (int)(Math.random()*10) == 1){
                     values[r][c] = -1;
                     bombs--;
                     if(bombs == 0)
                        return;
                  }
         }else if(col == 0){
            for(int r = 0; r < values.length; r++)
               for(int c = 0; c < values[r].length; c++)
                  if((r<row-1 || r>row+1 || c>col+1) && values[r][c] >= 0 && (int)(Math.random()*10) == 1){
                     values[r][c] = -1;
                     bombs--;
                     if(bombs == 0)
                        return;
                  }
         }else{
            for(int r = 0; r < values.length; r++)
               for(int c = 0; c < values[r].length; c++)
                  if((r<row-1 || r>row+1 || c<col-1 || c>col+1) && values[r][c] >= 0 && (int)(Math.random()*10) == 1){
                     values[r][c] = -1;
                     bombs--;
                     if(bombs == 0)
                        return;
                  }
         }
      
      }
   
   }
   
   public void win(){
      label.setText("YOU WON!!!!");
      for(int r = 0; r < values.length; r++)
         for(int c = 0; c < values[r].length; c++)
            if(values[r][c] == -1)
               buttons[r][c].setBackground(Color.green);
      reset.setEnabled(true);
   }
   
   public void massReveal(int r, int c){
      if(r >= 0 && r < revealed.length && c >= 0 && c < revealed[r].length && revealed[r][c] == 0){
         revealed[r][c] = 1;
         buttons[r][c].setText("" + values[r][c]);
         spaces--;
         if(values[r][c] == 0){
            massReveal(r, c-1);
            massReveal(r-1, c-1);
            massReveal(r-1, c);
            massReveal(r+1, c);
            massReveal(r+1, c+1);
            massReveal(r, c+1);
            massReveal(r-1, c+1);
            massReveal(r+1, c-1);
         }
      }
   }
   
   public void revealBombs(){
      for(int r = 0; r < values.length; r++)
         for(int c = 0; c < values[r].length; c++)
            if(values[r][c] == -1 && revealed[r][c] == 0){
               buttons[r][c].setBackground(Color.red);
               revealed[r][c] = 1;
            }
            else if( values[r][c] >= 0 && revealed[r][c] == 0){
               buttons[r][c].setText("" + values[r][c]);
            }
   }
   
   public static int countBombs(int r, int c) {
      int bomb = 0;
      Stack<Integer> stack = new Stack<Integer>();
      
      if(r == 0 && c == 0){
         for(int row = r; row <= r+1; row++)
            for(int col = c; col <= c+1; col++)
               stack.push(values[row][col]);
      }else if(r == 0){
         for(int row = r; row <= r+1; row++)
            for(int col = c - 1; col < values[row].length && col <= c+1; col++)
               stack.push(values[row][col]);
      }else if(c==0){
         for(int row = r - 1; row < values.length && row <= r+1; row++)
            for(int col = c; col <= c+1; col++)
               stack.push(values[row][col]);
      }else{
         for(int row = r - 1; row < values.length && row <= r+1; row++)
            for(int col = c - 1; col < values[row].length && col <= c+1; col++)
               stack.push(values[row][col]);
      }
      
      while(!stack.isEmpty()){
         if(stack.pop() == -1)
            bomb++;
      }
            
      return bomb;
   }
   
   private class Class2 implements MouseListener{
      int row;
      int col;
      
      public Class2(int r, int c){
         row = r;
         col = c;
      }
      
      public void mousePressed(MouseEvent e){
         if(e.getButton() == MouseEvent.BUTTON3){
            if(revealed[row][col] == 0){
               label.setText("Number of flags " + --flag);
               buttons[row][col].setBackground(Color.blue);
               revealed[row][col] = -1;
            }
            else if(revealed[row][col] == -1){
               label.setText("Number of flags " + ++flag);
               buttons[row][col].setBackground(null);
               revealed[row][col] = 0;
            }
         }
      }
      
      public void mouseExited(MouseEvent e){
      
      }
      public void mouseEntered(MouseEvent e){
      
      }
      public void mouseClicked(MouseEvent e){
      
      }
      public void mouseReleased(MouseEvent e){
      
      }
   
   }
   
  
   
   
   private class Class1 implements ActionListener{
      int row;
      int col;
      
      public Class1(int r, int c) {
         row = r;
         col = c;
      }
      
      public void actionPerformed(ActionEvent e) {
         if(firstMove){
            firstMove = false;
            setBombs(row, col);
            for(int r=0; r<values.length; r++){     //just using this to be able to see the values array currently
               System.out.println();
               for(int c=0; c<values[r].length;c++) {
                  if(values[r][c] != -1)
                     values[r][c] = countBombs(r, c);
               
                  System.out.print(" " + values[r][c] + " ");
               }
            }
            label.setText("Number of flags: " + flag);
         }
         
         
         if(revealed[row][col] == 0){
            if(e.getSource() == buttons[row][col] && values[row][col] == -1)
            {
               reset.setEnabled(true);    	
               revealBombs();
               label.setText("Game Over!");
            }
            
            else {
               if(values[row][col] == 0){
                  massReveal(row, col);
               }
               else{
                  buttons[row][col].setText("" + values[row][col]);
                  spaces--;
               }
            }
            
            if(spaces == 0)
               win();
               
            revealed[row][col] = 1;
         }
         
      }
   }
   
   private class Class3 implements ActionListener{
	   public void actionPerformed(ActionEvent e) {
	       if(e.getSource() == reset) {
	           firstMove = true;
              flag = 99;
              spaces = 381;
              reset.setEnabled(false);    
	           for(int r=0; r<buttons.length; r++){    
	               for(int c=0; c<buttons[0].length;c++) {
	                  buttons[r][c].setText(null);
                     buttons[r][c].setBackground(null);
                     revealed[r][c] = 0;
                     values[r][c] = 0;
	               }
	            }
	            label.setText("Welcome to Minesweeper!");
	           
	       }
	   }
   }
   
//   public boolean resetBoard(){
//      firstMove = true;
//      
//   }
}
