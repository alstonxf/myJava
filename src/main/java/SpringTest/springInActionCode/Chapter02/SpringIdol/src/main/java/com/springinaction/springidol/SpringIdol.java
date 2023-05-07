package SpringTest.springInActionCode.Chapter02.SpringIdol.src.main.java.com.springinaction.springidol;

public class SpringIdol implements TalentCompetition {
  private Performer[] performers;
  
  public SpringIdol() {}
  
  public void run() {
    for (int i = 0; i < performers.length; i++) {
      Performer performer = performers[i];
      performer.perform();
      System.out.println("-----------------------");
    }
  }
  
  public void setPerformers(Performer[] performers) {
    this.performers = performers;
  }
}
