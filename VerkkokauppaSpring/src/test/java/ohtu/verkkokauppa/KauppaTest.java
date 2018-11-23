package ohtu.verkkokauppa;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class KauppaTest {

    Pankki pankki;
    Viitegeneraattori vige;
    Varasto varasto;

    Kauppa kauppa;

    @Before
    public void setUp() {
        pankki = mock(Pankki.class);
        vige = mock(Viitegeneraattori.class);
        varasto = mock(Varasto.class);

        kauppa = new Kauppa(varasto, pankki, vige);
    }
    
    @Test
    public void ostoksenJalkeenPankinMetodiaTilisiirtoKutsutaan() {
        when(varasto.saldo(1)).thenReturn(5);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 77));
        
        kauppa.aloitaAsiointi();
        kauppa.lisaaKoriin(1);
        kauppa.tilimaksu("matti", "12345");
        
        verify(pankki).tilisiirto(eq("matti"), anyInt(), eq("12345"), anyString(), eq(77));
    }

    @Test
    public void kahdenEriTuotteenOstoksenJalkeenPankinMetodiaTilisiirtoKutsutaan() {
        when(varasto.saldo(1)).thenReturn(5);
        when(varasto.saldo(2)).thenReturn(5);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 77));
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "piimä", 33));
        
        kauppa.aloitaAsiointi();
        kauppa.lisaaKoriin(1);
        kauppa.lisaaKoriin(2);
        kauppa.tilimaksu("matti", "12345");
        
        verify(pankki).tilisiirto(eq("matti"), anyInt(), eq("12345"), anyString(), eq(110));
    }

    @Test
    public void kahdenSamanTuotteenOstoksenJalkeenPankinMetodiaTilisiirtoKutsutaan() {
        when(varasto.saldo(1)).thenReturn(5);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 77));
        
        kauppa.aloitaAsiointi();
        kauppa.lisaaKoriin(1);
        kauppa.lisaaKoriin(1);
        kauppa.tilimaksu("matti", "12345");
        
        verify(pankki).tilisiirto(eq("matti"), anyInt(), eq("12345"), anyString(), eq(154));
    }

    @Test
    public void yhdenLoytyneenJaYhdenLoppuneenTuotteenJalkeenTilisiirtoKutsutaan() {
        when(varasto.saldo(1)).thenReturn(5);
        when(varasto.saldo(2)).thenReturn(0);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 77));
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "piimä", 33));
        
        kauppa.aloitaAsiointi();
        kauppa.lisaaKoriin(1);
        kauppa.lisaaKoriin(2);
        kauppa.tilimaksu("matti", "12345");
        
        verify(pankki).tilisiirto(eq("matti"), anyInt(), eq("12345"), anyString(), eq(77));
    }

    @Test
    public void uusiAsiointiTyhjentääVanhatTiedot() {
        when(varasto.saldo(1)).thenReturn(5);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 77));
        when(varasto.saldo(2)).thenReturn(5);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "piimä", 33));
        
        kauppa.aloitaAsiointi();
        kauppa.lisaaKoriin(1);
        kauppa.tilimaksu("matti", "12345");
        
        kauppa.aloitaAsiointi();
        kauppa.lisaaKoriin(2);
        kauppa.tilimaksu("luke", "54321");
        
        verify(pankki).tilisiirto(eq("luke"), anyInt(), eq("54321"), anyString(), eq(33));
        
    }
    
    @Test
    public void jokainenAsiointiPyytaaUudenViitteen() {
        when(varasto.saldo(1)).thenReturn(5);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 77));
        
        
        kauppa.aloitaAsiointi();
        kauppa.lisaaKoriin(1);
        kauppa.tilimaksu("matti", "12345");
        
        verify(vige, times(1)).uusi();
        
        kauppa.aloitaAsiointi();
        kauppa.lisaaKoriin(1);
        kauppa.tilimaksu("luke", "54321");
        
        verify(vige, times(2)).uusi();
        
    }
    
    @Test
    public void koristaPoistoVahentaaHinnanSummasta() {
        when(varasto.saldo(1)).thenReturn(5);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 77));
        when(varasto.saldo(2)).thenReturn(5);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "piimä", 33));
        
        kauppa.aloitaAsiointi();
        kauppa.lisaaKoriin(2);
        kauppa.lisaaKoriin(1);
        kauppa.poistaKorista(2);
        kauppa.tilimaksu("luke", "54321");
        
        verify(pankki).tilisiirto(eq("luke"), anyInt(), eq("54321"), anyString(), eq(77));        
    }
    
}
