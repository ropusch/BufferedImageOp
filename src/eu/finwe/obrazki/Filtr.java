package eu.finwe.obrazki;

//import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
//import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImageOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
//import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * Klasa abstrakcyjna ułatwiająca implementację interfejsu BufferedImageOp
 * w obrębie pakietu eu.finwe.Util
 * 
 * Obiecuje implementację interfejsu BufferedImageOp, ale z 4 wymaganych
 * metod dostarcza tylko dwóch, dlatego MUSI być abstrakcyjna ("niepełna",
 * "wymagająca uszczegółowienia przez dziedziczące po niej klasy")
 * 
 * @author Jacek Kawa
 * @since 1.5
 */


abstract class Filtr implements BufferedImageOp {

/* w komentarzach 2 metody interfejsu do zaimplementowania w klasach
 * pochodnych, a poniżej kolejne 2 metody: createCompatibleDestImage() 
 * oraz getRenderingHints() już zaimplemetnowane (to zamyka intefejs BufferedImageOp).
 * 
 * Dodatkowo w tej klasie znajduje się metoda getPoint2D_same(), która
 * ułatiwa implementację getPoint2D() przez klasy pochodne, ale nie
 * jest wymagana przez interfejs.
 */
    
    /** 
     * Performs a single-input/single-output operation on a BufferedImage. 
     * If the color models for the two images do not match, a color conversion 
     * into the destination color model is performed. If the destination image 
     * is null, a BufferedImage with an appropriate ColorModel is created.
     * 
     * @param src The BufferedImage to be filtered
     * @param dest The BufferedImage in which to store the results
     * @return The filtered BufferedImage. 
     */

    /*
    public BufferedImage filter(BufferedImage src, BufferedImage dest)
    {

    }
    */
   
    
/* trzeba zaimplementować dla każdego filtra osobno */

    /**
     * Returns the location of the corresponding destination point given 
     * a point in the source image. If dstPt is specified, it is used to hold 
     * the return value.
     * @param srcPt The Point2D that represents the point in the source image
     * @param dstPt The Point2D in which to store the result 
     * @return The Point2D in the destination image that corresponds to the 
     *          specified point in the source image.
     */
    /*
    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt)
    {

    }
    */
    
    /* można jednak stworzyć metody, które ułatwią budowanie pozostałych */
    /**
     * Jeśli dstPt <> null, kopiuje współrzędne z srcPt, jeśli == null,
     * tworzy nowy o wsp. takich samych jak srcPt. Zwraca zmanipulowany/stworzony
     * dstPt
     * 
     * @param srcPt wzorcowy punkt
     * @param dstPt punkt do zmiany/null
     * @return punkt zawierający współrzędne takie jak srcPt. ==dstPt, jeśli ten był <> null
     */
    protected static Point2D getPoint2D_same(Point2D srcPt, Point2D dstPt)
    {
        if (dstPt == null)
        {
            dstPt = (Point2D)srcPt.clone();
            return dstPt;
        }
        
        dstPt.setLocation(srcPt);
       
        return dstPt;

    }
    
    /**
     * Returns the bounding box of the filtered destination image. 
     * An IllegalArgumentException may be thrown if the source image is 
     * incompatible with the types of images allowed by the class implementing 
     * this filter.
     * 
     * @param src The BufferedImage to be filtered 
     * @return The Rectangle2D representing the destination image's bounding box.
     */
    /*public Rectangle2D getBounds2D(BufferedImage src)
    {
        // naiwne podejście polega na przefiltrowaniu obrazka
        // ale my tego NIE zrobimy, bo pozwolimy funkcjom filter()
        // korzystać z metody createCompatibleDestImage()
        // a tak doszłoby do zapętlenia...
        //BufferedImage tmp = filter(src, null);
        //Rectangle2D ret = tmp.getRaster().getBounds();

        return null;
    }*/

    
    /* i w końcu dwie metody zaimplementowane w całości */
    
    /**
     * Creates a zeroed destination image with the correct size and number of 
     * bands. An IllegalArgumentException may be thrown if the source image 
     * is incompatible with the types of images allowed by the class 
     * implementing this filter.
     * 
     * @param src The BufferedImage to be filtered
     * @param destCM ColorModel of the destination. If null, the ColorModel of the source is used. 
     * @return The zeroed destination image.
     */
    @Override
    public BufferedImage createCompatibleDestImage(BufferedImage src,
            ColorModel destCM)
    {
        // wymiary obrazka po filtracji
        Rectangle rct = (Rectangle)getBounds2D(src);
        
        if (destCM == null)
            destCM = src.getColorModel();
        
        WritableRaster retWR = destCM.createCompatibleWritableRaster(rct.width, rct.height);
        
        BufferedImage ret  = new BufferedImage(destCM, retWR, false, null);
         
        return ret;
    }


    /**
     * Returns the rendering hints for this operation.
     * 
     * @return The RenderingHints object for this BufferedImageOp. Returns null if no hints have been set.
     */
    @Override
    public RenderingHints getRenderingHints()
    {
        return null;
    }

    
    
}
