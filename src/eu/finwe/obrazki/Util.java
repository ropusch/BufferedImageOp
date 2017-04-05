package eu.finwe.obrazki;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.*;
import java.awt.Image;
import java.awt.font.FontRenderContext;
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


    /* Testowanie wybranych metod;
     * klas zawierających metodę main() może być w projekcie dużo.
     * Trzeba tylko pamiętać o określeniu tej "startowej"
     */
    public static void main(String args[]) throws IOException
    {
        
        String nazwaPliku = (args.length > 0) ? args[0] : "obrazek.png";
        
        // 1. Czytanie obrazka i zapisywanie wyników
        BufferedImage x = czytaj(nazwaPliku);
        if (x == null)
            System.exit(1);

        BufferedImage y = odbijPionowo(x);
        zapisz(y, "1_odbity" + nazwaPliku);
        
        y = skaluj(y, 2);
        zapisz(y, "2_skalowany" + nazwaPliku);
                
        
        Font f = new Font("Arial", Font.BOLD, 15);
        y = drukujNapisLG(y, "Jestem napisem", f, Color.yellow, 10, 10);
        zapisz(y, "3_napis" + nazwaPliku);
        
        
    }            
    
    
}
