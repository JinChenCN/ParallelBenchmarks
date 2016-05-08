package spec.benchmarks.xml.pt.transform;//####[7]####
//####[7]####
import java.io.File;//####[9]####
import java.io.FileInputStream;//####[10]####
import java.io.IOException;//####[11]####
import javax.xml.parsers.ParserConfigurationException;//####[13]####
import javax.xml.transform.Source;//####[14]####
import javax.xml.transform.Templates;//####[15]####
import javax.xml.transform.Transformer;//####[16]####
import javax.xml.transform.TransformerConfigurationException;//####[17]####
import javax.xml.transform.TransformerException;//####[18]####
import javax.xml.transform.TransformerFactory;//####[19]####
import javax.xml.transform.stream.StreamResult;//####[20]####
import org.xml.sax.SAXException;//####[21]####
import spec.benchmarks.xml.pt.XMLBenchmark;//####[23]####
import spec.harness.Constants;//####[24]####
import spec.harness.Context;//####[25]####
import spec.harness.Launch;//####[26]####
import spec.harness.Util;//####[27]####
import spec.harness.results.BenchmarkResult;//####[28]####
import spec.io.FileCache;//####[29]####
import java.util.Properties;//####[30]####
import spec.io.FileCache.CachedFile;//####[31]####
//####[31]####
//-- ParaTask related imports//####[31]####
import pt.runtime.*;//####[31]####
import java.util.concurrent.ExecutionException;//####[31]####
import java.util.concurrent.locks.*;//####[31]####
import java.lang.reflect.*;//####[31]####
import pt.runtime.GuiThread;//####[31]####
import java.util.concurrent.BlockingQueue;//####[31]####
import java.util.ArrayList;//####[31]####
import java.util.List;//####[31]####
//####[31]####
public class Main extends XMLBenchmark {//####[34]####
    static{ParaTask.init();}//####[34]####
    /*  ParaTask helper method to access private/protected slots *///####[34]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[34]####
        if (m.getParameterTypes().length == 0)//####[34]####
            m.invoke(instance);//####[34]####
        else if ((m.getParameterTypes().length == 1))//####[34]####
            m.invoke(instance, arg);//####[34]####
        else //####[34]####
            m.invoke(instance, arg, interResult);//####[34]####
    }//####[34]####
//####[35]####
    private static final int LONG_VALIDATION_MODE = 0;//####[35]####
//####[36]####
    private static final int SHORT_VALIDATION_MODE = 1;//####[36]####
//####[37]####
    private static final int SINGLE_LOOP_MODE = 0;//####[37]####
//####[38]####
    private static final int MULTIPLE_LOOP_MODE = 1;//####[38]####
//####[39]####
    private static final int INPUT_PAIR = 10;//####[39]####
//####[40]####
    private static final String CONTROL_FILE_NAME = "transformations.properties";//####[40]####
//####[41]####
    static String OUT_DIR = "xml_out";//####[41]####
//####[43]####
    private static final String[] XML_NAMES = { "chess-fo/Kasparov-Karpov.xml", "jenitennison/index.xml", "jenitennison/text.xml", "nitf/nitf-fishing.xml", "shared/REC-xml-19980210.xml", "recipes/recipes.xml", "dsd/article.xml", "renderx/chess/Kasparov-Karpov.xml", "renderx/examples/balance/balance_sheet.xml", "renderx/examples/meeting/meeting_minutes.xml" };//####[43]####
//####[56]####
    private static final String[] XSL_NAMES = { "chess-fo/chess.xsl", "jenitennison/page.xsl", "jenitennison/markup.xsl", "nitf/nitf-stylized.xsl", "spec-html/xmlspec.xsl", "recipes/recipes.xsl", "dsd/article2html.xsl", "renderx/chess/chess.xsl", "renderx/examples/balance/balance_sheet.xsl", "renderx/examples/meeting/meeting_minutes.xsl" };//####[56]####
//####[87]####
    private static final int[] loops = { 2, 18, 31, 34, 1, 10, 12, 3, 11, 23 };//####[87]####
//####[100]####
    private static FileCache.CachedFile[] xmlInput;//####[100]####
//####[101]####
    private static FileCache.CachedFile[] xslInput;//####[101]####
//####[102]####
    private static int validationMode = LONG_VALIDATION_MODE;//####[102]####
//####[103]####
    private static int loopMode = SINGLE_LOOP_MODE;//####[103]####
//####[104]####
    private static Properties longValidationProperties;//####[104]####
//####[105]####
    private static Properties[] shortValidationProperties;//####[105]####
//####[106]####
    private static String validationFileName;//####[106]####
//####[107]####
    private static Transformer[][] allTransformers;//####[107]####
//####[109]####
    private static void setValidationMode(int mode) {//####[109]####
        validationMode = mode;//####[110]####
    }//####[111]####
//####[113]####
    private static int getValidationMode() {//####[113]####
        return validationMode;//####[114]####
    }//####[115]####
//####[117]####
    private static void setLoopMode(int mode) {//####[117]####
        loopMode = mode;//####[118]####
    }//####[119]####
//####[121]####
    private static int getLoopMode() {//####[121]####
        return loopMode;//####[122]####
    }//####[123]####
//####[125]####
    public static String testType() {//####[125]####
        return MULTI;//####[126]####
    }//####[127]####
//####[129]####
    public static void setupBenchmark() {//####[129]####
        String tmpName = Util.getProperty(Constants.XML_TRANSFORM_OUT_DIR_PROP, null);//####[130]####
        OUT_DIR = tmpName != null ? tmpName : OUT_DIR;//####[131]####
        File file = new File(OUT_DIR);//####[132]####
        validationFileName = getFullName(Main.class, null, CONTROL_FILE_NAME);//####[133]####
        xmlInput = new FileCache.CachedFile[INPUT_PAIR];//####[134]####
        xslInput = new FileCache.CachedFile[INPUT_PAIR];//####[135]####
        for (int i = 0; i < INPUT_PAIR; i++) //####[136]####
        {//####[136]####
            xmlInput[i] = getCachedFile(Main.class, null, XML_NAMES[i]);//####[137]####
            xslInput[i] = getCachedFile(Main.class, null, XSL_NAMES[i]);//####[138]####
        }//####[139]####
        longValidationProperties = new Properties();//####[140]####
        try {//####[141]####
            if (!file.exists()) //####[142]####
            {//####[142]####
                file.mkdir();//####[143]####
            }//####[144]####
            longValidationProperties.load(new FileInputStream(validationFileName));//####[145]####
        } catch (IOException e) {//####[146]####
            e.printStackTrace();//####[147]####
        }//####[148]####
        setupTransformers();//####[150]####
        setValidationMode(LONG_VALIDATION_MODE);//####[152]####
        setLoopMode(SINGLE_LOOP_MODE);//####[153]####
        Main main = new Main(new BenchmarkResult(), 1);//####[154]####
        main.harnessMain();//####[155]####
        int threads = Launch.currentNumberBmThreads;//####[156]####
        shortValidationProperties = new Properties[Launch.currentNumberBmThreads];//####[157]####
        Properties outProperties = main.getOutProperties();//####[158]####
        for (int i = 0; i < threads; i++) //####[159]####
        {//####[159]####
            shortValidationProperties[i] = (Properties) outProperties.clone();//####[160]####
        }//####[161]####
        setValidationMode(SHORT_VALIDATION_MODE);//####[162]####
        setLoopMode(MULTIPLE_LOOP_MODE);//####[163]####
    }//####[164]####
//####[166]####
    public static void tearDownBenchmark() {//####[166]####
        if (!ExtOutputStream.wasFullVerificationError && !Util.getBoolProperty(Constants.XML_TRANSFORM_LEAVE_OUT_DIR_PROP, null)) //####[167]####
        {//####[168]####
            remove(new File(OUT_DIR));//####[169]####
        }//####[170]####
    }//####[171]####
//####[174]####
    private static void setupTransformers() {//####[174]####
        allTransformers = new Transformer[Launch.currentNumberBmThreads][INPUT_PAIR];//####[175]####
        try {//####[176]####
            TransformerFactory transformerFactory = TransformerFactory.newInstance();//####[177]####
            for (int i = 0; i < INPUT_PAIR; i++) //####[178]####
            {//####[178]####
                Templates precompiledTemplates = transformerFactory.newTemplates(xslInput[i].asNewStreamSource());//####[179]####
                for (int j = 0; j < Launch.currentNumberBmThreads; j++) //####[181]####
                {//####[181]####
                    allTransformers[j][i] = precompiledTemplates.newTransformer();//####[182]####
                }//####[183]####
            }//####[184]####
        } catch (TransformerConfigurationException e) {//####[185]####
            e.printStackTrace();//####[186]####
        } catch (IOException e) {//####[187]####
            e.printStackTrace();//####[188]####
        }//####[189]####
    }//####[190]####
//####[192]####
    private Transformer[] precompiledTransformers;//####[192]####
//####[193]####
    private BaseOutputStream outputStream;//####[193]####
//####[194]####
    private StreamResult streamResult;//####[194]####
//####[196]####
    public Main(BenchmarkResult bmResult, int threadId) {//####[196]####
        super(bmResult, threadId);//####[197]####
        if (getValidationMode() == LONG_VALIDATION_MODE) //####[198]####
        {//####[198]####
            setOutputStream(new ExtOutputStream());//####[199]####
            setValidationProperties(longValidationProperties);//####[200]####
        } else {//####[201]####
            setOutputStream(new BaseOutputStream());//####[202]####
            setValidationProperties(shortValidationProperties[threadId - 1]);//####[203]####
        }//####[204]####
        precompiledTransformers = allTransformers[threadId - 1];//####[206]####
    }//####[207]####
//####[209]####
    public void harnessMain() {//####[209]####
        try {//####[210]####
            long start = System.currentTimeMillis();//####[211]####
            for (int i = 0; i < 3; i++) //####[214]####
            {//####[214]####
                executeWorkload();//####[217]####
            }//####[218]####
            long time = System.currentTimeMillis() - start;//####[224]####
            System.out.println("Parallel xml transform has taken  " + (time / 1000.0) + " seconds.");//####[225]####
        } catch (Exception e) {//####[226]####
            e.printStackTrace(Context.getOut());//####[227]####
        }//####[228]####
    }//####[229]####
//####[231]####
    public void setOutputStream(BaseOutputStream stream) {//####[231]####
        outputStream = stream;//####[232]####
        streamResult = new StreamResult(outputStream);//####[233]####
    }//####[234]####
//####[236]####
    public static void main(String[] args) throws Exception {//####[236]####
        runSimple(Main.class, args);//####[237]####
    }//####[238]####
//####[240]####
    private void setValidationProperties(Properties props) {//####[240]####
        outputStream.setValidationProperties(props);//####[241]####
    }//####[242]####
//####[244]####
    private Properties getOutProperties() {//####[244]####
        if (outputStream instanceof ExtOutputStream) //####[245]####
        {//####[245]####
            return ((ExtOutputStream) outputStream).getOutProperties();//####[246]####
        }//####[247]####
        return null;//####[248]####
    }//####[249]####
//####[251]####
    private void executeWorkload() throws TransformerException, ParserConfigurationException, SAXException, IOException {//####[252]####
        TaskIDGroup g;//####[254]####
        g = new TaskIDGroup(INPUT_PAIR);//####[255]####
        for (int i = 0; i < INPUT_PAIR; i++) //####[256]####
        {//####[256]####
            String propertyNamePrefix = XML_NAMES[i] + ".";//####[257]####
            int loops = (getLoopMode() == SINGLE_LOOP_MODE) ? 1 : Main.loops[i];//####[258]####
            Transformer transformer = precompiledTransformers[i];//####[262]####
            TaskID id = doTransform(loops, xmlInput[i], transformer, propertyNamePrefix);//####[266]####
            g.add(id);//####[267]####
        }//####[269]####
        try {//####[270]####
            g.waitTillFinished();//####[271]####
        } catch (Exception e) {//####[272]####
            e.printStackTrace();//####[273]####
        }//####[274]####
    }//####[275]####
//####[277]####
    private static volatile Method __pt__doTransform_int_CachedFile_Transformer_String_method = null;//####[277]####
    private synchronized static void __pt__doTransform_int_CachedFile_Transformer_String_ensureMethodVarSet() {//####[277]####
        if (__pt__doTransform_int_CachedFile_Transformer_String_method == null) {//####[277]####
            try {//####[277]####
                __pt__doTransform_int_CachedFile_Transformer_String_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__doTransform", new Class[] {//####[277]####
                    int.class, CachedFile.class, Transformer.class, String.class//####[277]####
                });//####[277]####
            } catch (Exception e) {//####[277]####
                e.printStackTrace();//####[277]####
            }//####[277]####
        }//####[277]####
    }//####[277]####
    private TaskID<Void> doTransform(Object loops, Object xmlInput, Object transformer, Object propertyNamePrefix) throws TransformerException, ParserConfigurationException, SAXException, IOException {//####[278]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[278]####
        return doTransform(loops, xmlInput, transformer, propertyNamePrefix, new TaskInfo());//####[278]####
    }//####[278]####
    private TaskID<Void> doTransform(Object loops, Object xmlInput, Object transformer, Object propertyNamePrefix, TaskInfo taskinfo) throws TransformerException, ParserConfigurationException, SAXException, IOException {//####[278]####
        // ensure Method variable is set//####[278]####
        if (__pt__doTransform_int_CachedFile_Transformer_String_method == null) {//####[278]####
            __pt__doTransform_int_CachedFile_Transformer_String_ensureMethodVarSet();//####[278]####
        }//####[278]####
        List<Integer> __pt__taskIdIndexList = new ArrayList<Integer>();//####[278]####
        List<Integer> __pt__queueIndexList = new ArrayList<Integer>();//####[278]####
        if (loops instanceof BlockingQueue) {//####[278]####
            __pt__queueIndexList.add(0);//####[278]####
        }//####[278]####
        if (loops instanceof TaskID) {//####[278]####
            taskinfo.addDependsOn((TaskID)loops);//####[278]####
            __pt__taskIdIndexList.add(0);//####[278]####
        }//####[278]####
        if (xmlInput instanceof BlockingQueue) {//####[278]####
            __pt__queueIndexList.add(1);//####[278]####
        }//####[278]####
        if (xmlInput instanceof TaskID) {//####[278]####
            taskinfo.addDependsOn((TaskID)xmlInput);//####[278]####
            __pt__taskIdIndexList.add(1);//####[278]####
        }//####[278]####
        if (transformer instanceof BlockingQueue) {//####[278]####
            __pt__queueIndexList.add(2);//####[278]####
        }//####[278]####
        if (transformer instanceof TaskID) {//####[278]####
            taskinfo.addDependsOn((TaskID)transformer);//####[278]####
            __pt__taskIdIndexList.add(2);//####[278]####
        }//####[278]####
        if (propertyNamePrefix instanceof BlockingQueue) {//####[278]####
            __pt__queueIndexList.add(3);//####[278]####
        }//####[278]####
        if (propertyNamePrefix instanceof TaskID) {//####[278]####
            taskinfo.addDependsOn((TaskID)propertyNamePrefix);//####[278]####
            __pt__taskIdIndexList.add(3);//####[278]####
        }//####[278]####
        int[] __pt__queueIndexArray = new int[__pt__queueIndexList.size()];//####[278]####
        for (int __pt__i = 0; __pt__i < __pt__queueIndexArray.length; __pt__i++) {//####[278]####
            __pt__queueIndexArray[__pt__i] = __pt__queueIndexList.get(__pt__i);//####[278]####
        }//####[278]####
        taskinfo.setQueueArgIndexes(__pt__queueIndexArray);//####[278]####
        if (__pt__queueIndexArray.length > 0) {//####[278]####
            taskinfo.setIsPipeline(true);//####[278]####
        }//####[278]####
        int[] __pt__taskIdIndexArray = new int[__pt__taskIdIndexList.size()];//####[278]####
        for (int __pt__i = 0; __pt__i < __pt__taskIdIndexArray.length; __pt__i++) {//####[278]####
            __pt__taskIdIndexArray[__pt__i] = __pt__taskIdIndexList.get(__pt__i);//####[278]####
        }//####[278]####
        taskinfo.setTaskIdArgIndexes(__pt__taskIdIndexArray);//####[278]####
        taskinfo.setParameters(loops, xmlInput, transformer, propertyNamePrefix);//####[278]####
        taskinfo.setMethod(__pt__doTransform_int_CachedFile_Transformer_String_method);//####[278]####
        taskinfo.setInstance(this);//####[278]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[278]####
    }//####[278]####
    public void __pt__doTransform(int loops, CachedFile xmlInput, Transformer transformer, String propertyNamePrefix) throws TransformerException, ParserConfigurationException, SAXException, IOException {//####[278]####
        Transformer[] transformerForEach = new Transformer[loops];//####[279]####
        for (int i = loops - 1; i >= 0; i--) //####[280]####
        {//####[280]####
            transformerForEach[i] = transformer;//####[281]####
        }//####[282]####
        for (int j = loops - 1; j >= 0; j--) //####[283]####
        {//####[283]####
            transform(transformerForEach[j], createSaxSource(xmlInput), propertyNamePrefix + "SAX", j);//####[284]####
            transform(transformer, createDomSource(xmlInput), propertyNamePrefix + "DOM", j);//####[285]####
            transform(transformer, xmlInput.asNewStreamSource(), propertyNamePrefix + "Stream", j);//####[286]####
        }//####[287]####
    }//####[288]####
//####[288]####
//####[290]####
    private void transform(Transformer transformer, Source source, String descr, int loop) throws TransformerException, ParserConfigurationException, SAXException, IOException {//####[291]####
        transformer.reset();//####[292]####
        synchronized (this) {//####[293]####
            outputStream.setCurrentProp(descr);//####[294]####
            transformer.transform(source, streamResult);//####[295]####
            outputStream.checkResult(loop);//####[296]####
        }//####[297]####
    }//####[299]####
}//####[299]####
