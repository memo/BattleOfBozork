package test.tdl;


import utilities.JEasyFrame;


public class TDLGridTest {
    public static void main(String[] args) throws Exception {
        // Model model = new SmartModel();
        int size = 15;
        // CostModel model = new CostModel(size);
        Model model = new Model(size);
        model.lambda = 0.0;
        model.epsilon = 0.1;
        model.alpha = 0.1;
        model.makeView();
        // Thread.sleep(5000);
        // model.cheat();
        int nEpisodes = 101;
        model.rewards = new double[]{-1,0};
        // model.rewards = new double[]{0, 1};
        // model.makeMaze();

        // new Scanner(System.in).nextLine();
        System.out.println(model.run(nEpisodes));
        // model.findCosts();
        

        new JEasyFrame(new View(model, model.v), "Value function");
        // new JEasyFrame(new ArrayView(model.cost), "Value cost function", true);
        // PlotFrame.easyPlot("Steps to goal", model.steps);
        // PlotFrame.easyPlot("Abs Error", model.err);

//        String title = "Steps to Goal";
//        ArrayDataSeries ads = new ArrayDataSeries(model.steps, "");
//        DataSeriesGroup dsg = new DataSeriesGroup(ads);
//        dsg.xLabel = "episode";
//        dsg.yLabel = "nSteps";
//        dsg.setRangeY(new Range(0, 500));
//        new PlotFrame(dsg, "Steps to Goal", true);
//
//        Easy.save(model.steps, "gridtdl.xml");

        double pSuccess = ((double) model.nSuccess) / model.nSteps;
        System.out.println("nSuccess: " + model.nSuccess);
        System.out.println("nSteps: " + model.nSteps);
        System.out.println("Entropy: " + entropy(pSuccess));
        System.out.println("Bits of Information: " + model.nSteps * entropy(pSuccess));

//        // switch off learning
        model.alpha = 0.0;
        model.epsilon = 0.0;
        model.selfEval();
        model.cheat();
        model.selfEval();
//        // now test
//        // new JEasyFrame(new ArrayView(model.v), "Value function", true);
//        System.out.println(model.run(nEpisodes));

        double p = 0.01;
        System.out.println(900 * (1 - entropy(p)));
    }

    public static double entropy(double p) {
        return - (( p * log2(p) ) +
                  ( (1-p) * log2(1-p) ));
    }

    public static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }

}
