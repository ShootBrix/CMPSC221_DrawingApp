/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java2ddrawingapplication;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
//import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
//import java.awt.Paint;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
//import java.awt.event.MouseMotionAdapter;
//import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
//import java.awt.*;
//import java.awt.event.*;
//import java.awt.geom.Ellipse2D;
//import java.awt.geom.Line2D;
//import java.awt.geom.Point2D;
import java.util.ArrayList;
//import javax.swing.*;
import javax.swing.border.*;



public class DrawingApplicationFrame extends JFrame{
    
    private final JFrame frame;

    // Create the panels for the top of the application. One panel for each
    // line and one to contain both of those panels.
    private final JPanel line1;   // will hold undo,clear, shape:, and Filled
    private final JPanel line2;   // will hold Gradient, 1st and 2nd color, line width, Dash length, and Dashed checkbox
    private final JPanel top;     // will contain line1 and line1.
    private final JPanel bottom;  // will contain mouse position.
    
    // create the widgets for the firstLine Panel.
    private final JButton undo, clear;
    public final JComboBox shapes;
    private final JCheckBox filled;            
            
    //create the widgets for the secondLine Panel.
    private final JCheckBox gradient, dashed;
    private final JButton color1, color2;
    private final JTextField lineWidth, dashLength;
    private final JLabel width, dash;
    
    // Variables for drawPanel.
    private DrawPanel drawPanel;
    
    // add status label
    private final JLabel mousePos;
    
    // Constructor for DrawingApplicationFrame
    public DrawingApplicationFrame(){
        
        //setupt frame
        frame = new JFrame("Java 2D Drawings");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(650,500);
        frame.setVisible(true);
        
        //set up top panel
        top = new JPanel();
        top.setLayout(new GridLayout(2,1));
        frame.getContentPane().add(top, BorderLayout.PAGE_START);
        
        //create 2 JPanels and add to top panel
        line1 = new JPanel();
        top.add(line1);
        line2 = new JPanel();
        top.add(line2);
        
        //set up drawing panel
        drawPanel = new DrawPanel();
        frame.getContentPane().add(drawPanel, BorderLayout.CENTER);
       
        //set up bottom panel
        bottom = new JPanel();
        bottom.setLayout(new BorderLayout());
        frame.getContentPane().add(bottom, BorderLayout.PAGE_END);
        
        //creating the dropbox for shapes
        shapes = new JComboBox<>(new String[]{"Rectangle", "Oval", "Line"});
        
        //create color bottons
        color1 = new JButton("1st Color");
        color1.setBackground(Color.BLACK);
        color1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                    color1.setBackground(JColorChooser.showDialog(null, "Pick your color", Color.BLACK));
            }
        });
        
        color2 = new JButton("2nd Color");
        color2.setBackground(Color.WHITE);
        color2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                    color2.setBackground(JColorChooser.showDialog(null, "Pick your color", Color.BLACK));
            }
        });
        
        // creating widgets
        undo = new JButton("Undo");
        undo.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent event){
                drawPanel.undo();
            }
        });
        
        clear = new JButton("Clear");
        clear.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent event){
                drawPanel.clear();
            }
        });
        
        gradient = new JCheckBox("Use Gradient");
        filled = new JCheckBox("Filled");
        dashed = new JCheckBox("Dashed");
        dashLength = new JTextField("15");
        lineWidth = new JTextField("10");
        dash = new JLabel("Dash Length:");
        width = new JLabel("Line Width:");
        
        //adding componenets to line 1
        line1.add(undo);
        line1.add(clear);
        line1.add(shapes);
        line1.add(filled);
        
        //adding componenets to line 2
        line2.add(gradient);
        line2.add(color1);
        line2.add(color2);
        line2.add(width);
        line2.add(lineWidth);
        line2.add(dash);
        line2.add(dashLength);
        line2.add(dashed);
        
        mousePos = new JLabel("( , )");
        bottom.add(mousePos, BorderLayout.WEST);
        
        drawPanel.setLayout(new GridLayout());
        drawPanel.setVisible(true);
        drawPanel.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(0, 0, 20, 30)));
        drawPanel.setBackground(Color.WHITE);
                   
    }

    // Create event handlers, if needed

    // Create a private inner class for the DrawPanel.
    private class DrawPanel extends JPanel{
        private ArrayList<MyShapes> myShapeArray = new ArrayList();
        private MyShapes currentShape = null;
        
        public DrawPanel(){
            MouseHandler mouseHandler = new MouseHandler();
            this.addMouseListener(mouseHandler);
            this.addMouseMotionListener(mouseHandler);
        }

        public void paintComponent(Graphics g){
            
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            if(currentShape != null){
                currentShape.draw(g2d);
            }
            //loop through and draw each shape in the shapes arraylist
            for (MyShapes myShape : myShapeArray){
                myShape.draw(g2d);
            }
            g2d.dispose();
        }

        public void clear(){
            myShapeArray.clear();
            currentShape = null;
            repaint();
        }
        
        public void undo(){
            int index = myShapeArray.size() - 1;
            myShapeArray.remove(index);
            currentShape = null;
            repaint();
        }

        private class MouseHandler extends MouseAdapter implements MouseMotionListener{
            
            private Point PointA;
            private Point PointB;
            
            private MyShapes CreateShape(){
                
                MyShapes temp = null;
                
                //combo box selection variable
                String selected = (String)shapes.getSelectedItem();
                
                //variables to use in if-statements:
                int widthInt = Integer.parseInt(lineWidth.getText());
                int dashInt = Integer.parseInt(dashLength.getText());
                boolean filledBool = false;
                
                //create basic stroke:
                Stroke stroke = new BasicStroke(widthInt, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                
                if (dashed.isSelected()){
                    stroke = new BasicStroke(widthInt, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 10, new float[]{dashInt}, 0);
                }
                
                if (filled.isSelected()){
                    filledBool = true;
                }
                
                if ("Rectangle".equalsIgnoreCase(selected)) {
                    temp = new MyRectangle(PointA, PointB, color1.getBackground(), stroke, filledBool);
                }
                else if ("Oval".equalsIgnoreCase(selected)) {
                    temp = new MyOval(PointA, PointB, color1.getBackground(), stroke, filledBool);
                }
                else if ("Line".equalsIgnoreCase(selected)) {
                    temp = new MyLine(PointA, PointB, color1.getBackground(), stroke);
                }
                 
                if (gradient.isSelected()){
                    temp.setPaint( new GradientPaint((float)PointA.getX(),(float)PointA.getY(),color1.getBackground(),
                            (float)PointB.getX(),(float)PointA.getY(),color2.getBackground(),true) );
                }
                
                return temp;
                
            }
            
            @Override
            public void mousePressed(MouseEvent event){
                PointA = new Point(event.getPoint().x, event.getPoint().y);
            }
            
            @Override
            public void mouseReleased(MouseEvent event){
                PointB = new Point(event.getPoint().x, event.getPoint().y);
                myShapeArray.add(CreateShape());
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent event){
                PointB = new Point(event.getPoint().x, event.getPoint().y);
                currentShape = CreateShape();
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent event){
                String position = "(" + event.getPoint().x + "," + event.getPoint().y + ")";
                mousePos.setText(position);
            }
            
        }//MouseHandler
        
    }//DrawPanel
    
}//DrawingApplicationFrame
