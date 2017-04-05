package eu.finwe.obrazki;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.*;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.HashMap;
import javax.imageio.*;

/**
 *
 * @author jfk
 * @version 0.3
 * 
 * <pre>Kilka użytecznych funkcji manipulujących obrazkami</pre>
 */
public class Util {
    
    /**
     * Użyteczna metoda do szybkiego czytania plików graficznych
     * 
     * @param nazwa nazwa pliku z obrazem
     * @return obiekt BufferedImage zawierający dane obrazu lub null w przypadku niepowodzenia
     * 
     */
    public static BufferedImage czytaj(String nazwa)
    {
        BufferedImage ret = null;
        try{
            ret = ImageIO.read(new File(nazwa));
        } 
        catch (IOException e)
        {
            
        }
        return ret;
    }
    
    
    /**
     * zapisuje obraz pod wskazaną nazwą. Format obrazu wyjściowego określany
     * jest na podstawie rozszerzenia
     * 
     * obsługiwanie rozszerzenia: jpg, jpeg, png; w przypadku nierozpoznanego
     * rozszerzenia, plik zapisywany jest jako jpeg.
     * 
     * @param m obraz do zapisania-
     * @param nazwa nazwa pliku docelowego
     * @throws java.io.IOException w przypadku niepowodzenia przy zapisie
     * 
     * @since 0.2
     */
    public static void zapisz(final BufferedImage m, String nazwa) throws IOException
    {
        /** mapa rozszerzenie -> format; dla rozszerzeń, których brak
         * używany jest typ pliku "jpeg"
         */
        HashMap<String, String> mapa = new HashMap<String, String>();
        mapa.put("png", "png");
        mapa.put("jpg", "jpg");
        mapa.put("jpeg", "jpg");


        // określ format na podstawie rozszerzenia
        String rozszerzenie = 
                nazwa.substring(nazwa.lastIndexOf(".") + 1, nazwa.length());
        
        // na wszelki wypadek
        rozszerzenie = rozszerzenie.toLowerCase();
        
        // domyślnie jpeg
        String format = "jpg";
        if (mapa.containsKey(rozszerzenie))
            format = mapa.get(rozszerzenie);
                
        
        ImageIO.write(m, format, new File(nazwa));
    }    
    
    /**
     * Tworzy czysty (wyzerowany) obraz w identycznym modelu kolorów i o identycznych
     * rozmiarach, jak obraz wzorcowy
     * 
     * @param src obraz wzorcowy
     * @return czysty obraz
     */
    private static BufferedImage czystyObraz(final BufferedImage src)
    {
        int w = src.getWidth();
        int h = src.getHeight();
        
        ColorModel cm = src.getColorModel();
        
        return czystyObraz(cm, w, h);
    }    

    /**
     * Tworzy czysty obraz w podanym modelu kolorów i o podanych wymiarach
     * 
     * @param cm model kolorów
     * @param szerokosc szerokość obrazka
     * @param wysokosc wysokość obrazka
     * @return
     */
    private static BufferedImage czystyObraz(ColorModel cm, int szerokosc, int wysokosc)
    {
        WritableRaster wr = cm.createCompatibleWritableRaster(szerokosc, wysokosc);
     
        return new BufferedImage(cm, wr, false, null);
    }
    
           
    /**
     * Kopiuje obiekt src
     * 
     * @param src obraz źródłowy
     * @return dokładna kopia obrazu
     */
    public static BufferedImage kopiuj(final BufferedImage src)
    {
        BufferedImage dst = czystyObraz(src);
        /** obiekt graphics zapewnia podstawowe operacje graficzne na 
         * przypisanym mu "płótnie".
         */
        Graphics2D g = dst.createGraphics();
        /* Tutaj płótnem jest obrazek, więc
         * rysujemy obrazek na obrazku
         */
        g.drawImage(src, null, 0, 0);
        return dst;
    }
    
    /**
     * Kopiuje piksele z src do dst przy założeniu, że wymiary się zgadzają.
     * Dokonuje przy okazji konwersji dla modeli kolorów w obu obrazkach.
     * 
     * @param src obraz źródłowy
     * @param dst obraz docelowy
     * @throws może rzucić IllegalArgumentException(), gdy wymiary się nie zgadzają
     */
    private static void kopiujW(final BufferedImage src, BufferedImage dst)
    {
        int w = src.getWidth();
        int h = src.getHeight();
        
        if (w != dst.getWidth() || h != dst.getHeight())
            throw new IllegalArgumentException("wymiary obrazów nie pasują");
        
        int [] bufor = src.getRGB(0, 0, w, h, null, 0, w);
        dst.setRGB(0, 0, w, h, bufor, 0, w);
    }
    
    /**
     * Tworzy obraz odbity względem pionowej osi obrazu oryginalnego.
     * Zwraca wynik jako nowy obraz.
     * 
     * @param src obraz źródłowy
     * @return nowy obraz w tym samym modelu kolorów zawierający obraz oryginalny odbity poziomo
     */
    public static BufferedImage odbijPoziomo(final BufferedImage src)
    {
        BufferedImage ret = czystyObraz(src);
        int w = src.getWidth();
        int h = src.getHeight();
        
        Graphics2D g = ret.createGraphics();
        g.drawImage(src, 0, 0, w, h, 
                         w, 0, 0, h, null);
        
        return ret;
    }
    
    /**
     * Tworzy obraz odbity względem poziomej osi obrazu oryginalnego.
     * Zwraca wynik jako nowy obraz.
     * 
     * @param src obraz źródłowy
     * @return nowy obraz w tym samym modelu kolorów zawierający obraz oryginalny odbity pionowo
     */
    public static BufferedImage odbijPionowo(final BufferedImage src)
    {
        BufferedImage ret = czystyObraz(src);
        int w = src.getWidth();
        int h = src.getHeight();
        
        Graphics2D g = ret.createGraphics();
        g.drawImage(src, 0, 0, w, h, 
                         0, h, w, 0, null);
        return ret;
                
    }
    
    /**
     * Tworzy obraz w modelu kolorów obrazka źródłowego przeskalowany proporcjonalnie
     * zgodnie ze podaną skalą.
     * 
     * @param src obraz źródłowy
     * @param skala skala operacji, dodatni współczynnik
     * @return nowy obraz - wynik przeskalowania
     */    
    public static BufferedImage skaluj(final BufferedImage src, double skala) 
                                throws IllegalArgumentException
    {
        if (skala < 0)
            throw new IllegalArgumentException("skala powinna być nieujemna");

        int w = src.getWidth();
        int h = src.getHeight();
        
        w = (int)(w * skala);
        h = (int)(h * skala);
        
        return skaluj(src, w, h);
        
    }
    

    /**
     * Tworzy obraz w modelu kolorów obrazka źródłowego przeskalowany do
     * podanych wymiarów
     * 
     * @param src obraz źródłowy
     * @param szerokosc szerokosc obrazu wynikowego (w pikselach)
     * @param wysokosc wysokosc obrazu wynikowego (w pikselach)
     * @return nowy obraz - wynik przeskalowania
     */    
    public static BufferedImage skaluj(final BufferedImage src, 
                int szerokosc, int wysokosc) throws IllegalArgumentException
    {
        if (szerokosc < 0 || wysokosc < 0)
            throw new IllegalArgumentException("wysokosc i szerokosc powinny byc liczbami dodatnimi");
        
        BufferedImage ret = czystyObraz(src.getColorModel(), szerokosc, wysokosc);
        Image tmp = src.getScaledInstance(szerokosc, wysokosc, Image.SCALE_DEFAULT);
        Graphics2D g = ret.createGraphics();
        g.drawImage(tmp, 0, 0, null);
        return ret;
    }
    
    /**
     * Tworzy nowy obraz zawierający dane obrazu _obraz_ przekształcone do 
     * modelu kolorów określonego przez obraz _wzorzec_
     * 
     * @param wzorzec obraz dostarczający informację o modelu kolorów
     * @param obraz obraz do konwersji
     * @return nowy obraz zawierający wynik operacji
     */
    public static BufferedImage dopasujTyp(final BufferedImage wzorzec, BufferedImage obraz)
    {
        BufferedImage ret = czystyObraz(wzorzec.getColorModel(), obraz.getWidth(), obraz.getHeight());
        kopiujW(obraz, ret);
        return ret;
    }
    
    /**
     * Zwraca obrazek będący wynikiem mnożenia (a właściwie średnią geometryczną) 
     * obrazów podanych jako argumenty
     * 
     * @param im1 pierwszy czynnik
     * @param im2 drugi czynnik
     * @return wynik mnozenia - nowy obraz
     */
    public static BufferedImage mnoz(final BufferedImage im1, final BufferedImage im2)
    {
        BufferedImage ret = czystyObraz(im1);
        
        int w = im1.getWidth();
        int h = im1.getHeight();
        
        BufferedImage tmp = dopasujTyp(im1, skaluj(im2, w, h));
                
        double [] piksele1;
        double [] piksele2; 
        piksele1 = im1.getRaster().getPixels(0, 0, w, h, (double []) null);
        piksele2 = tmp.getRaster().getPixels(0, 0, w, h, (double []) null);
        
        double [] wynik = new double[piksele1.length];
        for (int i = 0; i < piksele1.length; i++)
        {
            wynik[i] = Math.sqrt(piksele1[i] * piksele2[i]);
        }
        
        WritableRaster r = ret.getRaster();
        
        r.setPixels(0, 0, w, h, wynik);
        ret.setData(r);
        
        return ret;
    }

    /**
     * Zwraca kopię obrazka z dodanym napisem. Współrzędne podawane są
     * dla punktu 0,0 w lewym górnym rogu obrazka.
     * 
     * @param im obrazek oryginalny
     * @param napis napis do dodania
     * @param czcionka wybrana czcionka napisu
     * @param kolor wybrany kolor
     * @param odLewej pozycja od lewej (piksele)
     * @param odGory pozycja od góry (piksele)
     * @return nowy obrazek z dodanym napisem
     * @since 0.2
     */
    public static BufferedImage drukujNapisLG(BufferedImage im, String napis,
            Font czcionka, Color kolor, int odLewej, int odGory)
    {
        BufferedImage ret = kopiuj(im);
        Graphics2D g = ret.createGraphics();
        g.setFont(czcionka);
        g.setColor(kolor);
        
        g.drawString(napis, odLewej, odGory);
       
        return ret;
    }
    
    /**
     * Zwraca kopię obrazka z dodanym napisem. Współrzędne podawane są
     * dla punktu 0,0 w prawym dolnym rogu obrazka.
     * 
     * @param im obrazek oryginalny
     * @param napis napis do dodania
     * @param czcionka wybrana czcionka napisu
     * @param kolor wybrany kolor
     * @param odPrawej pozycja od prawej (piksele)
     * @param odDolu pozycja od dołu (piksele)
     * @return nowy obrazek z dodanym napisem
     * @since 0.2
     */
    public static BufferedImage drukujNapisPD(BufferedImage im, String napis,
            Font czcionka, Color kolor, int odPrawej, int odDolu)
    {
        // wymiary obrazka
        int w = im.getWidth();
        int h = im.getHeight();

        // potrzebne do pomiaru parametrów wymiaru nadrukowanego tekstu        
        Graphics2D g = im.createGraphics();
        FontRenderContext fr = g.getFontRenderContext();
        // "bounding box"
        Rectangle2D rec = czcionka.getStringBounds(napis, fr);

        // długość i wysokość tekstu
        int textWidth = (int)rec.getWidth();
        int textHeight = (int)rec.getHeight();
        
        // skoro ma być zostawione od dołu i prawej, to musimy odsunąć od 
        // tych krawędzi
        
        int fromLeft = w - odPrawej - textWidth;
        int fromTop = h - odDolu - textHeight;
        
        if (fromLeft < 0)
            fromLeft = 0;
        if (fromTop < 0)
            fromTop = 0;
        
        return drukujNapisLG(im, napis, czcionka, kolor, fromLeft, fromTop);
    }
    

    /*
     * publiczny enum, żeby dało się wybrać typ filtra
     */
    
    /**
     * Dostępne typy filtrów dla metody dajFiltr()
     * 
     * @since 1.5
     */
    public static enum typFiltra
    {
        SKALUJ, ODBIJ_POZIOMO, ODBIJ_PIONOWO, OPISZ, MNOZ;
    }
    

    /*
     * prywatna tablica mieszająca ułatwia przechowywanie niektórych, 
     * gotowych filtrów jeśli były już stworzone
     * 
     * 
     * sekcja statyczna umożliwia JEDNOKROTNE dla klasy zainicjalizowanie
     * struktury, która będzie potem wspólna dla wszystkich instancji
     */
    private static HashMap<typFiltra, BufferedImageOp> gotoweFiltry = null;    
    static {
        gotoweFiltry = new HashMap<Util.typFiltra, BufferedImageOp>();
    }
    
    /**
     * Metoda - "fabryka" dla interfejsu BufferedImageOp. Produkuje instancje
     * klas wewnętrznych zgodnych z interfejsem BufferedImageOp, implementujących
     * różne operacje zdefiniowane powyżej
     * 
     * @param typ Util.typFiltra.{SKALUJ, ODBIJ_POZIOMO, ODBIJ_PIONOWO itd.} 
     * @param parametry filtra, jeśli takowych wymaga lub null, jeśli nie.
     *        Parametry jako pary klucz => wartość umieszczane są w tablicy
     *        mieszającej.
     * 
     * Parametrów wymagają:
     * 1. Filtr skalujący typFiltra.SKALUJ: 
     *      "skala" => "1.0", gdzie 1.0 to współczynnik skalowania
     * 
     * @return instancja klasy zgodnej z interfejsem BufferedimageOp implementująca
     *         wybrany filtr
     */
    public static BufferedImageOp dajFiltr(typFiltra typ, HashMap<String, String> parametry)
    {
       
        switch(typ)
        {
            case ODBIJ_PIONOWO:
            case ODBIJ_POZIOMO:
                // te filtry się nie zmieniają, więc za każdym razem, jak ktoś
                // poprosi, to dostanie ten sam
                if (!gotoweFiltry.containsKey(typ))
                    gotoweFiltry.put(typ, new FiltrOdbijajacy(typ));
                 return gotoweFiltry.get(typ);

            case SKALUJ:
                 // ten filtr zależy od skali, więc za każdym razem 
                 // zwracany jest nowy                
                return new FiltrSkalujacy(parametry);
                         
            default:
                throw new IllegalArgumentException("Ten typ filtra nie jest dostępny");
                    
        }
    }
    
    
    
    // UWAGA: 
    // to jest klasa prywatna, rozszerzająca klasę abstrakcyjną filtr
    // o dostępie pakietowym. Innymi słowy z zewnątrz NIE JEST DOSTĘPNA.
    //
    // Klasa ta dziedziczy bo Filtr, bo dzięki temu nie musi implementować
    // createCompatibleDestImage() wymaganej przez interefejs BufferedImageOp.
    // Z faktu, że dziedziczy po Filtr wynika również siłą rzeczy, że 
    // implementuje interfejs BufferedImageOp, więc nie trzeba tutaj tago
    // osobno zaznaczać.
    //
    //
    // Klasa ta ma modyfikator static, który mówi, że jest specyficzna 
    // NIE dla jakiejś instancji klasy Util, a dla wszystkich jej wystąpień.
    // 
    // W PRAKTYCE:
    //
    // Klasa wewnętrzna może korzystać z metod i pól klasy zewnętrznej (nawet
    // tych prywatnych!). 
    // 
    // Jeśli modyfikatora static by nie było, to znaczy, że klasa
    // ta miałaby dostęp do danych konkretnej instancji klasy zewnętrznej, ale
    // oznaczałoby też, że taka instancja musiałaby istnieć, żeby stworzyć
    // instancję tej klasy (czyli x = new Util() a dopiero potem możnaby poprosić
    // x o stworzenie filtra x.dajFiltr()
    //
    // Tak ma dostęp tylko do pól statycznych, ale to tu wystarczy.
    //
    // REASUMUJĄC:
    // Jeśli byłaby niestatyczna, to nie dałoby się stworzyć jej instancji 
    // bez instancji klasy zewnętrznej, a to nam tu nie jest potrzebne, 
    // a wręcz przeszkadza.
    //
    // UWAGA 2:
    //
    // To ma sens: na zewnątrz ta klasa pojawia się jako zwrócona przez 
    // metodę dajFiltr(), czyli funkcjonuje jako "jakaś tam klasa, zgodna
    // z interfejsem BufferedImageOp". Kompilatora nie obchodzi, że
    // jej definicja jest prywatna itp. Ilekolwiek metody nie byłoby dla niej
    // zdefiniowanych tutaj, na zewnątrz widoczne będą tylko te 4 określone
    // przez interfejs BufferedImageOp.
    
    static private class FiltrSkalujacy extends Filtr 
    {
        private double skala = 1.0;
        /**
         * Tworzy filtr skalujący; 
         * 
         * @param parametry = new Hashtable<String, String>{"skala", "1.0"}
         */
        
        FiltrSkalujacy(HashMap<String, String> parametry)
        {
            if (!(parametry == null) && parametry.containsKey("skala"))
                try {
                    skala = Double.parseDouble(parametry.get("skala"));    
                } 
                catch (NumberFormatException e)
                {
                    throw new IllegalArgumentException("Podano zły parametr skali");
                }
        }
        
        /**
         * Tworzy filtr skalujący o skali 1.0
         */
        FiltrSkalujacy()
        {
            // wywołanie konstruktora z konstruktora
            this(null);
        }
        
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

        @Override
        public BufferedImage filter(BufferedImage src, BufferedImage dest)
        {
            // skalowanie
            BufferedImage tmp = Util.skaluj(src, skala);

            // jeśli dest jest dane, to _musimy_ skorzystać, jeśli nie, to
            // może się zdarzyć, ze tmp ma inny ColorModel niż src, dlatego
            // tak czy siak tworzymy dest
            if (dest == null)
            {
                ColorModel naszCM = src.getColorModel();
                dest = createCompatibleDestImage(src, naszCM);
            }
            
            // kopiuj w obrazek dest; to przy okazji przekonwertuje kolory
            // korzystamy zatem z metody PRYWATNEJ klasy zewnętrznej!
            kopiujW(tmp, dest);
            
            return dest;
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
        @Override
        public Rectangle2D getBounds2D(BufferedImage src)
        {
            int w = src.getWidth();
            int h = src.getHeight();
            
            w = (int)(w * skala);
            h = (int)(h * skala);
            
            Rectangle2D ret = new Rectangle(w, h);

            return ret;
        }
    
        /**
         * Returns the location of the corresponding destination point given 
         * a point in the source image. If dstPt is specified, it is used to hold 
         * the return value.
         * @param srcPt The Point2D that represents the point in the source image
         * @param dstPt The Point2D in which to store the result 
         * @return The Point2D in the destination image that corresponds to the 
         *          specified point in the source image.
         */        
        @Override
        public Point2D getPoint2D(Point2D srcPt, Point2D dstPt)
        {
            double x = srcPt.getX();
            double y = srcPt.getY();
            
            x *= skala;
            y *= skala;
            
            Point2D tmp = new Point.Double(x, y);
            
            // alternatywnie: 
            //return Filtr.getPoint2D_same(tmp, dstPt);
            
            if (dstPt != null)
                dstPt.setLocation(tmp);
            
            return tmp;
        }    

    }
    
    /* filtry, które nie wymagają dodatkowych parametrów */
    static private class FiltrOdbijajacy extends Filtr
    {
        private final typFiltra typ;

        public FiltrOdbijajacy(typFiltra typ) {
            this.typ = typ;
        }
        
        /* teraz uzupełnimy metody abstrakcyjnej klasy Filtr - w praktyce
           brakujące metody BufferedImageOp */
        
        @Override
        public BufferedImage filter(BufferedImage src, BufferedImage dest)
        {
            
            BufferedImage ret;

            switch(this.typ)
            {
                case ODBIJ_POZIOMO:
                    ret = Util.odbijPoziomo(src);
                    break;
                case ODBIJ_PIONOWO:
                    ret = Util.odbijPionowo(src);
                    break;
                default:
                    ret = src;
            }
            
            if (dest == null)
            {
                ColorModel naszCM = src.getColorModel();
                dest = createCompatibleDestImage(src, naszCM);
            }
            
            kopiujW(ret, dest);
            
            return dest;
        }
   
        @Override
        public Rectangle2D getBounds2D(BufferedImage src)
        {
            return src.getRaster().getBounds();
        }        
        
        @Override
        public Point2D getPoint2D(Point2D srcPt, Point2D dstPt)
        {

            switch (typ)
            {
                // nie da się określić położenia punktu bez wymiarów obrazka,
                // bazowego, więc nie ma wielkiego wyboru.
                // 
                // nie jest to w dobrym stylu... można by to obejść produkując
                // instancje filtra dla konkretnych obrazków
                //
                // BufferedImageOp x = new FiltrOdbijajacy(..., BufferedImage obraz);
                //
                // ale w tym [laboratoryjnym] przypadku nie ma większego sensu
                
                case ODBIJ_PIONOWO:
                case ODBIJ_POZIOMO:
                    throw new UnsupportedOperationException("Destination " + 
                            "point cannot be determined for this filter");
            }

            // korzystamy z metody pomocniczej klasy abstakcyjnej
            return Filtr.getPoint2D_same(srcPt, dstPt);
        }
        
    
        
        
    }
    
    
    /* Testowanie wybranych metod;
     * klas zawierających metodę main() może być w projekcie dużo.
     * Trzeba tylko pamiętać o określeniu tej "startowej"
     */
    public static void main(String args[])
    {
        
        String nazwaPliku = (args.length > 0) ? args[0] : "obrazek.png";
        
        /* inny przykład pokazujący zastosowanie tablicy mieszającej;
         * zwróć jednak uwage na to, jaka będzie kolejność wykonywania
         * operacji
         */
        HashMap<String, BufferedImageOp> op = 
                        new HashMap<String, BufferedImageOp>();
        
        BufferedImageOp el;
        
        // 1. standardowy filtr
        final float skala = 1f/9f;
        Kernel k = new Kernel(3, 3, new float[] {skala, skala, skala,
                                                 skala, skala, skala,
                                                 skala, skala, skala,});

        el = new ConvolveOp(k);
        op.put("wygladzanie", el);
        
        // 2. znieksztalcenie
        el = new AffineTransformOp(AffineTransform.getShearInstance(2, 2), null);
        op.put("znieksztalcenie", el);
        
        // 3. NASZE odbicia
        op.put("odbicieV", Util.dajFiltr(Util.typFiltra.ODBIJ_PIONOWO, null));
        op.put("odbicieH", Util.dajFiltr(Util.typFiltra.ODBIJ_POZIOMO, null));
        
        
        // 4. NASZE skalowanie
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("skala", "0.5");
        op.put("skala", Util.dajFiltr(Util.typFiltra.SKALUJ, param));
        
        // 4. Czytanie obrazka i zapisywanie wyników
        BufferedImage x = czytaj(nazwaPliku);
        if (x == null)
            System.exit(1);

        BufferedImage y = x;
        
        Integer i = 0;
        String sekwencja = "";

        // pętla foreach() !
        for (String key : op.keySet())
        {
            System.out.println("Próba filtra: " + key);
            BufferedImageOp bio = op.get(key);
            
            // test bez prealokowania
            BufferedImage y1 = bio.filter(x, null);
            
            // test z wykorzystaniem  createCompatibleDestImage()
            BufferedImage y2 = bio.createCompatibleDestImage(x, null);
            bio.filter(x, y2);
            
            // a przy okazji metoda mnóż...
            y2 = mnoz(x, y2);
            
            
            try {
                zapisz(y1, "_" + key + "p1.jpg");    
            } 
            catch (IOException e)
            {
                System.out.println("Zapisanie obrazka " + key + 
                        "(próba 1) nie powiodło się");
            }

            try {
                zapisz(y2, "_" + key + "p2.jpg");    
            } 
            catch (IOException e)
            {
                System.out.println("Zapisanie obrazka " + key + 
                        "(próba 2) nie powiodło się");
            }
            
            // przetwarzanie sekwencyjne
            System.out.println("sekwencja");
            sekwencja = sekwencja + "-" + key;
            y = bio.filter(y, null);

            try {
                zapisz(y, "_" + i.toString() + "_" + sekwencja + ".jpg");    
            }   
            catch (IOException e)
            {
                System.out.println("Zapisanie obrazka " + key + 
                        "(próba 3) nie powiodło się");
            }         
            i++;
            
            
        }
        
    }            
    
    
}
