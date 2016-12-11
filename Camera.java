import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Moments;

public class Camera extends javax.swing.JFrame implements MouseListener {

    private static final long serialVersionUID = 1L;
    // definitions
    private DaemonThread myThread = null;
    int count = 0;
    Point center;
    float[] radius = new float[1];
    Point prev = new Point();
    Point next = new Point();
    VideoCapture webSource = null;
    Mat frame = new Mat();
    Mat frame1 = new Mat();
    Mat frame2 = new Mat();

    Mat frame3 = new Mat();
    Mat comp = new Mat();
    MatOfByte mem = new MatOfByte();
    MatOfByte mem1 = new MatOfByte();
    MatOfByte mem2 = new MatOfByte();
    MatOfByte mem3 = new MatOfByte();
    boolean start = false;
    private MatOfInt convexHullMatOfInt;
    private ArrayList<Point> convexHullPointArrayList;
    private MatOfPoint convexHullMatOfPoint;
    private ArrayList<MatOfPoint> convexHullMatOfPointArrayList;
    private MatOfInt4 convexityDefects;
    private final Robot robot;
    private boolean left = false;
    private boolean right = false;
    private final int x = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    private final int y = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    private boolean SVM = false;
    private boolean threshold = false;
    /*private boolean first = true;
    int r_min=0;
    int g_min=0;
    int b_min=0;
    int r_max=0;
    int g_max=0;
    int b_max=0;
    private Scalar hsv_min = new Scalar(0,0,0,0);
    private Scalar hsv_max = new Scalar(0,0,0,0);*/
    
    
    @Override
    public void mouseClicked(MouseEvent e){
        /*java.awt.Point p = e.getLocationOnScreen();
        Color color = new Color(0,0,0,0);
        try {
            Robot rob = new Robot();
            color = rob.getPixelColor(p.x, p.y);
        } catch (AWTException ex) {
            Logger.getLogger(Camera.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(first == true){
            r_min=color.getRed();
            g_min=color.getGreen();
            b_min=color.getBlue();
            first=false;   
        }
        if(color.getRed()<r_min && first==false)
            r_min=color.getRed();
        if(color.getGreen()<g_min && first==false)
            g_min=color.getGreen();
        if(color.getBlue()<b_min && first==false)
            b_min=color.getBlue();
        if(color.getRed()>r_max)
            r_max=color.getRed();
        if(color.getGreen()>g_max)
            g_max=color.getGreen();
        if(color.getBlue()>b_max)
            b_max=color.getBlue();
        hsv_min = new Scalar(b_min,g_min,r_min,0);
        hsv_max = new Scalar(b_max,g_max,r_max,0);
        System.out.println(hsv_min+" "+hsv_max);
        */
    }
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    //class daemon
    class DaemonThread implements Runnable {

        protected volatile boolean runnable = true;

        @Override
        public void run() {
            synchronized (this) {
                while (runnable) {
                    if (webSource.grab()) {
                        try {
                            webSource.retrieve(frame);

                            Imgproc.cvtColor(frame, frame1, Imgproc.COLOR_BGR2HSV);
                            LBL.setText("Low H " + LB.getValue());
                            LGL.setText("Low S " + LG.getValue());
                            LRL.setText("Low V " + LR.getValue());
                            BL.setText("High H " + B.getValue());
                            GL.setText("High S " + G.getValue());
                            RL.setText("High V " + R.getValue());

                            Scalar hsv_min = new Scalar(LB.getValue(), LG.getValue(), LR.getValue());
                            Scalar hsv_max = new Scalar(B.getValue(), G.getValue(), R.getValue());
                            //System.out.println(SVM);
//                             Scalar hsv_min = new Scalar(0, 50, 50, 0);  
//                             Scalar hsv_max = new Scalar(6, 255, 255, 0);  
//                             Scalar hsv_min2 = new Scalar(175, 50, 50, 0);  
//                             Scalar hsv_max2 = new Scalar(179, 255, 255, 0); 
                            Core.inRange(frame1, hsv_min, hsv_max, frame2);
//                             Core.inRange(frame1, hsv_min, hsv_max, frame3);           
//                             Core.inRange(frame1, hsv_min2, hsv_max2, frame4);  
//                             Core.bitwise_or(frame3, frame4, frame2); 

                            Imgproc.erode(frame2, frame2, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)));
                            Imgproc.erode(frame2, frame2, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)));
                            Imgproc.dilate(frame2, frame2, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(8, 8)));
                            Imgproc.dilate(frame2, frame2, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(8, 8)));
                            List<MatOfPoint> contours = new ArrayList<>();
                            frame2.convertTo(frame2, CvType.CV_32SC1);
                            Imgproc.findContours(frame2, contours, new Mat(), Imgproc.RETR_FLOODFILL, Imgproc.CHAIN_APPROX_SIMPLE);
                            int largestindex = 0;
                            int largestcontour = 0;
                            MatOfPoint largecontour = null;
                            for (int i = 0; i < contours.size(); i++) {
                                double area = Imgproc.contourArea(contours.get(i));
                                if (largestcontour < area) {
                                    largestindex = i;
                                    largecontour = contours.get(i);
                                }
                            }
                            Imgproc.drawContours(frame, contours, largestindex, new Scalar(255, 100, 0), 2);
                            Core.rectangle(frame, new Point(jPanel1.getWidth() / 4, jPanel1.getHeight() / 4), new Point(jPanel1.getWidth() * 3 / 4, jPanel1.getHeight() * 3 / 4), new Scalar(0, 255, 0));
                            if (largecontour != null && SVM) {
                                findfingertips(largecontour);
                                Core.inRange(frame, new Scalar(0, 255, 255), new Scalar(0, 255, 255), frame3);
//                                System.out.println(x * (jPanel1.getWidth() / 2 - (center.x - jPanel1.getWidth() / 4)) / jPanel1.getWidth() * 2);
                                frame3.convertTo(frame3, CvType.CV_32SC1);
                                List<MatOfPoint> tips = new ArrayList<>();
                                Imgproc.findContours(frame3, tips, new Mat(), Imgproc.RETR_FLOODFILL, Imgproc.CHAIN_APPROX_SIMPLE);                                
                                left = tips.size() / 2 == 3;
                                right = tips.size() / 2 == 2;
                            }
                            if(threshold==false)
                                Highgui.imencode(".bmp", frame, mem);
                            else
                                Highgui.imencode(".bmp", frame2, mem);
                            Image im = ImageIO.read(new ByteArrayInputStream(mem.toArray()));
                            BufferedImage image = (BufferedImage) im;
                            Graphics g = jPanel1.getGraphics();

                            if (g.drawImage(getFlippedImage(image), 0, 0, jPanel1.getWidth(), jPanel1.getHeight(), 0, 0, image.getWidth(), image.getHeight(), null)) {
                                if (largecontour != null && SVM) {
//                                    if((int) (x * (jPanel1.getWidth() / 2 - (center.x - jPanel1.getWidth() / 4)) / jPanel1.getWidth() * 2) > 0 && (int) (y * (center.y - jPanel1.getHeight() / 4) / jPanel1.getHeight() * 2) > 0 && (int) (y * (center.y - jPanel1.getHeight() / 4) / jPanel1.getHeight() * 2) < y && (int) (x * (jPanel1.getWidth() / 2 - (center.x - jPanel1.getWidth() / 4)) / jPanel1.getWidth() * 2) < x)
                                    if(center != null)
                                    {
                                    robot.mouseMove((int) (x * (jPanel1.getWidth() / 2 - (center.x - jPanel1.getWidth() / 4)) / jPanel1.getWidth() * 2), (int) (y * (center.y - jPanel1.getHeight() / 4) / jPanel1.getHeight() * 2));
                                    }

                                    if (left) {
                                        robot.mousePress(InputEvent.BUTTON1_MASK);
                                    } else {
                                        robot.mouseRelease(InputEvent.BUTTON1_MASK);
                                    }
                                    if (right) {
                                        robot.mousePress(InputEvent.BUTTON3_MASK);
                                        robot.mouseRelease(InputEvent.BUTTON3_MASK);
                                    }
                                    g.setColor(Color.red);
                                    g.setFont(new Font("Open Sans", Font.PLAIN, 20));
                                    if(center != null)
                                    g.drawString((640 - (int) center.x) + "," + (int) center.y, 640 - (int) center.x, (int) center.y);
                                }
                            }
                            if (runnable == false) {
                                System.out.println("Going to wait()");
                                this.wait();
                            }
                        } catch (Exception ex) {
//                            System.out.println("Error");
                            ex.printStackTrace();
                            System.exit(0);
                        }
                    }
                }
            }
        }
    }

    public Camera() throws AWTException {
        this.robot = new Robot();
        initComponents();
        addMouseListener(this);
        webSource = new VideoCapture(0);
        myThread = new DaemonThread();
        Thread t = new Thread(myThread);
        t.setDaemon(true);
        myThread.runnable = true;
        t.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        settings = new JFrame();
        BGR = new JPanel();
        LB = new JSlider(0, 255);
        LG = new JSlider(0, 255);
        LR = new JSlider(0, 255);
        LBL = new JLabel();
        LGL = new JLabel();
        LRL = new JLabel();
        B = new JSlider(0, 179);
        G = new JSlider(0, 255);
        R = new JSlider(0, 255);
        BL = new JLabel();
        GL = new JLabel();
        RL = new JLabel();
        move = new JButton();
        Threshold=new JButton();
        Exit = new JButton();
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Mouseless");
        settings.setTitle("Settings");

        move.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!SVM) {
                    SVM = true;
                    move.setText("Stop Virtual Mouse");
                } else if (SVM) {
                    SVM = false;
                    move.setText("Start Virtual Mouse");
                }
            }
        });
        Threshold.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!threshold) {
                    threshold = true;
                    Threshold.setText("Normal");
                } else if (threshold) {
                    threshold = false;
                    Threshold.setText("Threshold");
                }
            }
        });
        Exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            System.exit(0);
            }
        });
        this.setSize(660, 520);
        settings.setSize(230, 520);
        settings.setLocation(670, 0);
        settings.setVisible(true);
        jPanel1.setSize(640, 480);
        jPanel1.setLocation(0, 0);
        this.add(jPanel1);
        settings.add(BGR);
        BGR.setLocation(0, 0);
        LB.setValue(0);
        LG.setValue(0);
        LR.setValue(0);
        B.setValue(255);
        G.setValue(255);
        R.setValue(255);
        move.setText("Start Virtual Mouse");
        Threshold.setText("Threshold");
        Exit.setText("Exit Virtual Mouse");
        BGR.add(LBL);
        BGR.add(LB);
        BGR.add(BL);
        BGR.add(B);
        BGR.add(LGL);
        BGR.add(LG);
        BGR.add(GL);
        BGR.add(G);
        BGR.add(LRL);
        BGR.add(LR);
        BGR.add(RL);
        BGR.add(R);
        BGR.add(move);
        BGR.add(Threshold);
        BGR.add(Exit);
    }

    public static void main(String args[]) {
        System.load("/usr/local/Cellar/opencv/2.4.12_2/share/OpenCV/java/libopencv_java2412.dylib");
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Camera().setVisible(true);
                } catch (AWTException ex) {
                    Logger.getLogger(Camera.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    private javax.swing.JPanel jPanel1;
    private JFrame settings;
    private JPanel BGR;
    private JSlider LB;
    private JSlider LG;
    private JSlider LR;
    private JLabel LBL;
    private JLabel LGL;
    private JLabel LRL;
    private JSlider B;
    private JSlider G;
    private JSlider R;
    private JLabel BL;
    private JLabel GL;
    private JLabel RL;
    private JButton move;
    private JButton Threshold;
    private JButton Exit;

    public static BufferedImage getFlippedImage(BufferedImage bi) {
        BufferedImage flipped = new BufferedImage(
                bi.getWidth(),
                bi.getHeight(),
                bi.getType());
        AffineTransform tran = AffineTransform.getTranslateInstance(bi.getWidth(), 0);
        AffineTransform flip = AffineTransform.getScaleInstance(-1d, 1d);
        tran.concatenate(flip);

        Graphics2D g = flipped.createGraphics();
        g.setTransform(tran);
        g.drawImage(bi, 0, 0, null);
        g.dispose();

        return flipped;
    }

    private void findfingertips(MatOfPoint largecontour) {

        convexHullMatOfInt = new MatOfInt();
        convexHullPointArrayList = new ArrayList<>();
        convexHullMatOfPoint = new MatOfPoint();
        convexHullMatOfPointArrayList = new ArrayList<>();
        convexityDefects = new MatOfInt4();
        Imgproc.convexHull(largecontour, convexHullMatOfInt, false);
        Imgproc.convexityDefects(largecontour, convexHullMatOfInt, convexityDefects);
        for (int j = 0; j < convexHullMatOfInt.toList().size(); j++) {
            convexHullPointArrayList.add(largecontour.toList().get(convexHullMatOfInt.toList().get(j)));
        }
        convexHullMatOfPoint.fromList(convexHullPointArrayList);
        convexHullMatOfPointArrayList.add(convexHullMatOfPoint);
        Moments mo = Imgproc.moments(convexHullMatOfPoint);
        center = new Point(mo.get_m10() / mo.get_m00(), mo.get_m01() / mo.get_m00());
        Core.circle(frame, center, 2, new Scalar(255, 0, 255), 5);
        for (int i = 0; i < convexHullPointArrayList.size(); i++) {
            prev = convexHullPointArrayList.get(i);
            if (i < convexHullPointArrayList.size() - 1) {
                next = convexHullPointArrayList.get(i + 1);
            }
            double dist = Math.sqrt(Math.pow((next.x - prev.x), 2) + Math.pow((next.y - prev.y), 2));
            Point palmleft = new Point();
            Point palmright = new Point();
            palmleft.x = 0;
            palmleft.y = center.y;
            palmright.x = jPanel1.getWidth();
            palmright.y = center.y;
            Core.line(frame, palmleft, palmright, new Scalar(0, 0, 255));
            if (dist > 28) {
                double angle1 = Math.atan2(palmleft.y - center.y, palmleft.x - center.x);
                double angle2 = Math.atan2(center.y - convexHullPointArrayList.get(i).y, center.x - convexHullPointArrayList.get(i).x);
                double angle = angle1 - angle2;
                if (angle < 3.0) {
                    Core.line(frame, prev, center, new Scalar(255, 255, 255));
                    Core.circle(frame, convexHullPointArrayList.get(i), 5, new Scalar(0, 255, 255), 10);
                }
            }
        }
    }
}