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
import javax.xml.transform.sax.SAXSource;//####[21]####
import javax.xml.transform.dom.DOMSource;//####[22]####
import org.xml.sax.SAXException;//####[23]####
import spec.benchmarks.xml.pt.XMLBenchmark;//####[25]####
import spec.harness.Constants;//####[26]####
import spec.harness.Context;//####[27]####
import spec.harness.Launch;//####[28]####
import spec.harness.Util;//####[29]####
import spec.harness.results.BenchmarkResult;//####[30]####
import spec.io.FileCache;//####[31]####
import java.util.Properties;//####[32]####
import spec.io.FileCache.CachedFile;//####[33]####
//####[33]####
//-- ParaTask related imports//####[33]####
import pt.runtime.*;//####[33]####
import java.util.concurrent.ExecutionException;//####[33]####
import java.util.concurrent.locks.*;//####[33]####
import java.lang.reflect.*;//####[33]####
import pt.runtime.GuiThread;//####[33]####
import java.util.concurrent.BlockingQueue;//####[33]####
import java.util.ArrayList;//####[33]####
import java.util.List;//####[33]####
//####[33]####
public class Main extends XMLBenchmark {//####[35]####
    static{ParaTask.init();}//####[35]####
    /*  ParaTask helper method to access private/protected slots *///####[35]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[35]####
        if (m.getParameterTypes().length == 0)//####[35]####
            m.invoke(instance);//####[35]####
        else if ((m.getParameterTypes().length == 1))//####[35]####
            m.invoke(instance, arg);//####[35]####
        else //####[35]####
            m.invoke(instance, arg, interResult);//####[35]####
    }//####[35]####
//####[36]####
    private static final int LONG_VALIDATION_MODE = 0;//####[36]####
//####[37]####
    private static final int SHORT_VALIDATION_MODE = 1;//####[37]####
//####[38]####
    private static final int SINGLE_LOOP_MODE = 0;//####[38]####
//####[39]####
    private static final int MULTIPLE_LOOP_MODE = 1;//####[39]####
//####[40]####
    private static final int INPUT_PAIR = 10;//####[40]####
//####[41]####
    private static final String CONTROL_FILE_NAME = "transformations.properties";//####[41]####
//####[42]####
    static String OUT_DIR = "xml_out";//####[42]####
//####[43]####
    private static int THREADSNUM = 10;//####[43]####
//####[44]####
    private static ParaTask.ScheduleType scheduleType = ParaTask.ScheduleType.WorkSharing;//####[44]####
//####[46]####
    private static final String[] XML_NAMES = { "chess-fo/Kasparov-Karpov.xml", "jenitennison/index.xml", "jenitennison/text.xml", "nitf/nitf-fishing.xml", "shared/REC-xml-19980210.xml", "recipes/recipes.xml", "dsd/article.xml", "renderx/chess/Kasparov-Karpov.xml", "renderx/examples/balance/balance_sheet.xml", "renderx/examples/meeting/meeting_minutes.xml" };//####[46]####
//####[59]####
    private static final String[] XSL_NAMES = { "chess-fo/chess.xsl", "jenitennison/page.xsl", "jenitennison/markup.xsl", "nitf/nitf-stylized.xsl", "spec-html/xmlspec.xsl", "recipes/recipes.xsl", "dsd/article2html.xsl", "renderx/chess/chess.xsl", "renderx/examples/balance/balance_sheet.xsl", "renderx/examples/meeting/meeting_minutes.xsl" };//####[59]####
//####[90]####
    private static final int[] loops = { 2, 18, 31, 34, 1, 10, 12, 3, 11, 23 };//####[90]####
//####[103]####
    private static FileCache.CachedFile[] xmlInput;//####[103]####
//####[104]####
    private static FileCache.CachedFile[] xslInput;//####[104]####
//####[105]####
    private static int validationMode = LONG_VALIDATION_MODE;//####[105]####
//####[106]####
    private static int loopMode = SINGLE_LOOP_MODE;//####[106]####
//####[107]####
    private static Properties longValidationProperties;//####[107]####
//####[108]####
    private static Properties[][][] shortValidationProperties;//####[108]####
//####[109]####
    private static String validationFileName;//####[109]####
//####[110]####
    private static Transformer[][] allTransformers;//####[110]####
//####[111]####
    private int threadId;//####[111]####
//####[113]####
    private static void setValidationMode(int mode) {//####[113]####
        validationMode = mode;//####[114]####
    }//####[115]####
//####[117]####
    private static int getValidationMode() {//####[117]####
        return validationMode;//####[118]####
    }//####[119]####
//####[121]####
    private static void setLoopMode(int mode) {//####[121]####
        loopMode = mode;//####[122]####
    }//####[123]####
//####[125]####
    private static int getLoopMode() {//####[125]####
        return loopMode;//####[126]####
    }//####[127]####
//####[129]####
    public static String testType() {//####[129]####
        return MULTI;//####[130]####
    }//####[131]####
//####[133]####
    public static void setupBenchmark() {//####[133]####
        String tmpName = Util.getProperty(Constants.XML_TRANSFORM_OUT_DIR_PROP, null);//####[134]####
        OUT_DIR = tmpName != null ? tmpName : OUT_DIR;//####[135]####
        File file = new File(OUT_DIR);//####[136]####
        validationFileName = getFullName(Main.class, null, CONTROL_FILE_NAME);//####[137]####
        xmlInput = new FileCache.CachedFile[INPUT_PAIR];//####[138]####
        xslInput = new FileCache.CachedFile[INPUT_PAIR];//####[139]####
        for (int i = 0; i < INPUT_PAIR; i++) //####[140]####
        {//####[140]####
            xmlInput[i] = getCachedFile(Main.class, null, XML_NAMES[i]);//####[141]####
            xslInput[i] = getCachedFile(Main.class, null, XSL_NAMES[i]);//####[142]####
        }//####[143]####
        longValidationProperties = new Properties();//####[145]####
        try {//####[146]####
            if (!file.exists()) //####[147]####
            {//####[147]####
                file.mkdir();//####[148]####
            }//####[149]####
            longValidationProperties.load(new FileInputStream(validationFileName));//####[150]####
        } catch (IOException e) {//####[151]####
            e.printStackTrace();//####[152]####
        }//####[153]####
        setupTransformers();//####[155]####
        setValidationMode(LONG_VALIDATION_MODE);//####[157]####
        setLoopMode(SINGLE_LOOP_MODE);//####[158]####
        shortValidationProperties = new Properties[Launch.currentNumberBmThreads][THREADSNUM][3];//####[160]####
        Main main = new Main(new BenchmarkResult(), 1);//####[162]####
        main.harnessMain();//####[163]####
        int threads = Launch.currentNumberBmThreads;//####[164]####
        setValidationMode(SHORT_VALIDATION_MODE);//####[169]####
        setLoopMode(MULTIPLE_LOOP_MODE);//####[170]####
    }//####[171]####
//####[173]####
    public static void tearDownBenchmark() {//####[173]####
        if (!ExtOutputStream.wasFullVerificationError && !Util.getBoolProperty(Constants.XML_TRANSFORM_LEAVE_OUT_DIR_PROP, null)) //####[174]####
        {//####[175]####
            remove(new File(OUT_DIR));//####[176]####
        }//####[177]####
    }//####[178]####
//####[181]####
    private static void setupTransformers() {//####[181]####
        allTransformers = new Transformer[Launch.currentNumberBmThreads][INPUT_PAIR];//####[182]####
        try {//####[183]####
            TransformerFactory transformerFactory = TransformerFactory.newInstance();//####[184]####
            for (int i = 0; i < INPUT_PAIR; i++) //####[185]####
            {//####[185]####
                Templates precompiledTemplates = transformerFactory.newTemplates(xslInput[i].asNewStreamSource());//####[186]####
                for (int j = 0; j < Launch.currentNumberBmThreads; j++) //####[188]####
                {//####[188]####
                    allTransformers[j][i] = precompiledTemplates.newTransformer();//####[189]####
                }//####[190]####
            }//####[191]####
        } catch (TransformerConfigurationException e) {//####[193]####
            e.printStackTrace();//####[194]####
        } catch (IOException e) {//####[195]####
            e.printStackTrace();//####[196]####
        }//####[197]####
    }//####[198]####
//####[200]####
    private Transformer[] precompiledTransformers;//####[200]####
//####[202]####
    private StreamResult streamResult;//####[202]####
//####[204]####
    public Main(BenchmarkResult bmResult, int threadId) {//####[204]####
        super(bmResult, threadId);//####[205]####
        this.threadId = threadId;//####[206]####
        precompiledTransformers = allTransformers[threadId - 1];//####[216]####
    }//####[217]####
//####[219]####
    public void harnessMain() {//####[219]####
        try {//####[220]####
            for (int i = 0; i < 3; i++) //####[223]####
            {//####[223]####
                executeWorkload();//####[226]####
            }//####[227]####
        } catch (Exception e) {//####[234]####
            e.printStackTrace(Context.getOut());//####[235]####
        }//####[236]####
    }//####[237]####
//####[244]####
    public static void main(String[] args) throws Exception {//####[244]####
        long start = System.currentTimeMillis();//####[245]####
        ParaTask.init();//####[246]####
        runSimple(Main.class, args);//####[247]####
        long time = System.currentTimeMillis() - start;//####[248]####
        System.out.println("Parallel xml transform has taken  " + (time / 1000.0) + " seconds.");//####[249]####
    }//####[250]####
//####[256]####
    private Properties getOutProperties(BaseOutputStream outputStream) {//####[256]####
        if (outputStream instanceof ExtOutputStream) //####[257]####
        {//####[257]####
            return ((ExtOutputStream) outputStream).getOutProperties();//####[258]####
        }//####[259]####
        return null;//####[260]####
    }//####[261]####
//####[263]####
    private void executeWorkload() throws TransformerException, ParserConfigurationException, SAXException, IOException {//####[264]####
        ParaTask.setScheduling(scheduleType);//####[266]####
        ParaTask.setThreadPoolSize(ParaTask.ThreadPoolType.ONEOFF, THREADSNUM);//####[267]####
        TaskIDGroup g;//####[269]####
        g = new TaskIDGroup(THREADSNUM);//####[270]####
        for (int i = 0; i < INPUT_PAIR; i++) //####[271]####
        {//####[271]####
            String propertyNamePrefix = XML_NAMES[i] + ".";//####[272]####
            int loops = (getLoopMode() == SINGLE_LOOP_MODE) ? 1 : Main.loops[i];//####[273]####
            Transformer transformer = precompiledTransformers[i];//####[277]####
            TaskID id = doTransform(loops, xmlInput[i], transformer, propertyNamePrefix, i);//####[281]####
            g.add(id);//####[282]####
        }//####[284]####
        try {//####[285]####
            g.waitTillFinished();//####[286]####
        } catch (Exception e) {//####[287]####
            e.printStackTrace();//####[288]####
        }//####[289]####
    }//####[290]####
//####[292]####
    private static volatile Method __pt__doTransform_int_CachedFile_Transformer_String_int_method = null;//####[292]####
    private synchronized static void __pt__doTransform_int_CachedFile_Transformer_String_int_ensureMethodVarSet() {//####[292]####
        if (__pt__doTransform_int_CachedFile_Transformer_String_int_method == null) {//####[292]####
            try {//####[292]####
                __pt__doTransform_int_CachedFile_Transformer_String_int_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__doTransform", new Class[] {//####[292]####
                    int.class, CachedFile.class, Transformer.class, String.class, int.class//####[292]####
                });//####[292]####
            } catch (Exception e) {//####[292]####
                e.printStackTrace();//####[292]####
            }//####[292]####
        }//####[292]####
    }//####[292]####
    private TaskID<Void> doTransform(Object loops, Object xmlInput, Object transformer, Object propertyNamePrefix, Object INPUT_PAIR_num) throws TransformerException, ParserConfigurationException, SAXException, IOException {//####[293]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[293]####
        return doTransform(loops, xmlInput, transformer, propertyNamePrefix, INPUT_PAIR_num, new TaskInfo());//####[293]####
    }//####[293]####
    private TaskID<Void> doTransform(Object loops, Object xmlInput, Object transformer, Object propertyNamePrefix, Object INPUT_PAIR_num, TaskInfo taskinfo) throws TransformerException, ParserConfigurationException, SAXException, IOException {//####[293]####
        // ensure Method variable is set//####[293]####
        if (__pt__doTransform_int_CachedFile_Transformer_String_int_method == null) {//####[293]####
            __pt__doTransform_int_CachedFile_Transformer_String_int_ensureMethodVarSet();//####[293]####
        }//####[293]####
        List<Integer> __pt__taskIdIndexList = new ArrayList<Integer>();//####[293]####
        List<Integer> __pt__queueIndexList = new ArrayList<Integer>();//####[293]####
        if (loops instanceof BlockingQueue) {//####[293]####
            __pt__queueIndexList.add(0);//####[293]####
        }//####[293]####
        if (loops instanceof TaskID) {//####[293]####
            taskinfo.addDependsOn((TaskID)loops);//####[293]####
            __pt__taskIdIndexList.add(0);//####[293]####
        }//####[293]####
        if (xmlInput instanceof BlockingQueue) {//####[293]####
            __pt__queueIndexList.add(1);//####[293]####
        }//####[293]####
        if (xmlInput instanceof TaskID) {//####[293]####
            taskinfo.addDependsOn((TaskID)xmlInput);//####[293]####
            __pt__taskIdIndexList.add(1);//####[293]####
        }//####[293]####
        if (transformer instanceof BlockingQueue) {//####[293]####
            __pt__queueIndexList.add(2);//####[293]####
        }//####[293]####
        if (transformer instanceof TaskID) {//####[293]####
            taskinfo.addDependsOn((TaskID)transformer);//####[293]####
            __pt__taskIdIndexList.add(2);//####[293]####
        }//####[293]####
        if (propertyNamePrefix instanceof BlockingQueue) {//####[293]####
            __pt__queueIndexList.add(3);//####[293]####
        }//####[293]####
        if (propertyNamePrefix instanceof TaskID) {//####[293]####
            taskinfo.addDependsOn((TaskID)propertyNamePrefix);//####[293]####
            __pt__taskIdIndexList.add(3);//####[293]####
        }//####[293]####
        if (INPUT_PAIR_num instanceof BlockingQueue) {//####[293]####
            __pt__queueIndexList.add(4);//####[293]####
        }//####[293]####
        if (INPUT_PAIR_num instanceof TaskID) {//####[293]####
            taskinfo.addDependsOn((TaskID)INPUT_PAIR_num);//####[293]####
            __pt__taskIdIndexList.add(4);//####[293]####
        }//####[293]####
        int[] __pt__queueIndexArray = new int[__pt__queueIndexList.size()];//####[293]####
        for (int __pt__i = 0; __pt__i < __pt__queueIndexArray.length; __pt__i++) {//####[293]####
            __pt__queueIndexArray[__pt__i] = __pt__queueIndexList.get(__pt__i);//####[293]####
        }//####[293]####
        taskinfo.setQueueArgIndexes(__pt__queueIndexArray);//####[293]####
        if (__pt__queueIndexArray.length > 0) {//####[293]####
            taskinfo.setIsPipeline(true);//####[293]####
        }//####[293]####
        int[] __pt__taskIdIndexArray = new int[__pt__taskIdIndexList.size()];//####[293]####
        for (int __pt__i = 0; __pt__i < __pt__taskIdIndexArray.length; __pt__i++) {//####[293]####
            __pt__taskIdIndexArray[__pt__i] = __pt__taskIdIndexList.get(__pt__i);//####[293]####
        }//####[293]####
        taskinfo.setTaskIdArgIndexes(__pt__taskIdIndexArray);//####[293]####
        taskinfo.setParameters(loops, xmlInput, transformer, propertyNamePrefix, INPUT_PAIR_num);//####[293]####
        taskinfo.setMethod(__pt__doTransform_int_CachedFile_Transformer_String_int_method);//####[293]####
        taskinfo.setInstance(this);//####[293]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[293]####
    }//####[293]####
    public void __pt__doTransform(int loops, CachedFile xmlInput, Transformer transformer, String propertyNamePrefix, int INPUT_PAIR_num) throws TransformerException, ParserConfigurationException, SAXException, IOException {//####[293]####
        for (int j = loops - 1; j >= 0; j--) //####[296]####
        {//####[296]####
            transform(transformer, createSaxSource(xmlInput), propertyNamePrefix + "SAX", j, INPUT_PAIR_num);//####[297]####
            transform(transformer, createDomSource(xmlInput), propertyNamePrefix + "DOM", j, INPUT_PAIR_num);//####[298]####
            transform(transformer, xmlInput.asNewStreamSource(), propertyNamePrefix + "Stream", j, INPUT_PAIR_num);//####[299]####
        }//####[300]####
    }//####[323]####
//####[323]####
//####[325]####
    private static volatile Method __pt__doTransformTask_int_CachedFile_Transformer_String_int_method = null;//####[325]####
    private synchronized static void __pt__doTransformTask_int_CachedFile_Transformer_String_int_ensureMethodVarSet() {//####[325]####
        if (__pt__doTransformTask_int_CachedFile_Transformer_String_int_method == null) {//####[325]####
            try {//####[325]####
                __pt__doTransformTask_int_CachedFile_Transformer_String_int_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__doTransformTask", new Class[] {//####[325]####
                    int.class, CachedFile.class, Transformer.class, String.class, int.class//####[325]####
                });//####[325]####
            } catch (Exception e) {//####[325]####
                e.printStackTrace();//####[325]####
            }//####[325]####
        }//####[325]####
    }//####[325]####
    private TaskID<Void> doTransformTask(Object loops, Object xmlInput, Object transformer, Object propertyNamePrefix, Object INPUT_PAIR_num) throws TransformerException, ParserConfigurationException, SAXException, IOException {//####[326]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[326]####
        return doTransformTask(loops, xmlInput, transformer, propertyNamePrefix, INPUT_PAIR_num, new TaskInfo());//####[326]####
    }//####[326]####
    private TaskID<Void> doTransformTask(Object loops, Object xmlInput, Object transformer, Object propertyNamePrefix, Object INPUT_PAIR_num, TaskInfo taskinfo) throws TransformerException, ParserConfigurationException, SAXException, IOException {//####[326]####
        // ensure Method variable is set//####[326]####
        if (__pt__doTransformTask_int_CachedFile_Transformer_String_int_method == null) {//####[326]####
            __pt__doTransformTask_int_CachedFile_Transformer_String_int_ensureMethodVarSet();//####[326]####
        }//####[326]####
        List<Integer> __pt__taskIdIndexList = new ArrayList<Integer>();//####[326]####
        List<Integer> __pt__queueIndexList = new ArrayList<Integer>();//####[326]####
        if (loops instanceof BlockingQueue) {//####[326]####
            __pt__queueIndexList.add(0);//####[326]####
        }//####[326]####
        if (loops instanceof TaskID) {//####[326]####
            taskinfo.addDependsOn((TaskID)loops);//####[326]####
            __pt__taskIdIndexList.add(0);//####[326]####
        }//####[326]####
        if (xmlInput instanceof BlockingQueue) {//####[326]####
            __pt__queueIndexList.add(1);//####[326]####
        }//####[326]####
        if (xmlInput instanceof TaskID) {//####[326]####
            taskinfo.addDependsOn((TaskID)xmlInput);//####[326]####
            __pt__taskIdIndexList.add(1);//####[326]####
        }//####[326]####
        if (transformer instanceof BlockingQueue) {//####[326]####
            __pt__queueIndexList.add(2);//####[326]####
        }//####[326]####
        if (transformer instanceof TaskID) {//####[326]####
            taskinfo.addDependsOn((TaskID)transformer);//####[326]####
            __pt__taskIdIndexList.add(2);//####[326]####
        }//####[326]####
        if (propertyNamePrefix instanceof BlockingQueue) {//####[326]####
            __pt__queueIndexList.add(3);//####[326]####
        }//####[326]####
        if (propertyNamePrefix instanceof TaskID) {//####[326]####
            taskinfo.addDependsOn((TaskID)propertyNamePrefix);//####[326]####
            __pt__taskIdIndexList.add(3);//####[326]####
        }//####[326]####
        if (INPUT_PAIR_num instanceof BlockingQueue) {//####[326]####
            __pt__queueIndexList.add(4);//####[326]####
        }//####[326]####
        if (INPUT_PAIR_num instanceof TaskID) {//####[326]####
            taskinfo.addDependsOn((TaskID)INPUT_PAIR_num);//####[326]####
            __pt__taskIdIndexList.add(4);//####[326]####
        }//####[326]####
        int[] __pt__queueIndexArray = new int[__pt__queueIndexList.size()];//####[326]####
        for (int __pt__i = 0; __pt__i < __pt__queueIndexArray.length; __pt__i++) {//####[326]####
            __pt__queueIndexArray[__pt__i] = __pt__queueIndexList.get(__pt__i);//####[326]####
        }//####[326]####
        taskinfo.setQueueArgIndexes(__pt__queueIndexArray);//####[326]####
        if (__pt__queueIndexArray.length > 0) {//####[326]####
            taskinfo.setIsPipeline(true);//####[326]####
        }//####[326]####
        int[] __pt__taskIdIndexArray = new int[__pt__taskIdIndexList.size()];//####[326]####
        for (int __pt__i = 0; __pt__i < __pt__taskIdIndexArray.length; __pt__i++) {//####[326]####
            __pt__taskIdIndexArray[__pt__i] = __pt__taskIdIndexList.get(__pt__i);//####[326]####
        }//####[326]####
        taskinfo.setTaskIdArgIndexes(__pt__taskIdIndexArray);//####[326]####
        taskinfo.setParameters(loops, xmlInput, transformer, propertyNamePrefix, INPUT_PAIR_num);//####[326]####
        taskinfo.setMethod(__pt__doTransformTask_int_CachedFile_Transformer_String_int_method);//####[326]####
        taskinfo.setInstance(this);//####[326]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[326]####
    }//####[326]####
    public void __pt__doTransformTask(int loops, CachedFile xmlInput, Transformer transformer, String propertyNamePrefix, int INPUT_PAIR_num) throws TransformerException, ParserConfigurationException, SAXException, IOException {//####[326]####
        for (int j = loops - 1; j >= 0; j--) //####[327]####
        {//####[327]####
            transform(transformer, createSaxSource(xmlInput), propertyNamePrefix + "SAX", j, INPUT_PAIR_num);//####[328]####
            transform(transformer, createDomSource(xmlInput), propertyNamePrefix + "DOM", j, INPUT_PAIR_num);//####[329]####
            transform(transformer, xmlInput.asNewStreamSource(), propertyNamePrefix + "Stream", j, INPUT_PAIR_num);//####[330]####
        }//####[331]####
    }//####[332]####
//####[332]####
//####[334]####
    private void transform(Transformer transformer, Source source, String descr, int loop, int INPUT_PAIR_num) throws TransformerException, ParserConfigurationException, SAXException, IOException {//####[335]####
        transformer.reset();//####[336]####
        BaseOutputStream outputStream;//####[337]####
        StreamResult streamResult;//####[338]####
        int sourceType = source instanceof SAXSource ? 0 : (source instanceof DOMSource ? 1 : 2);//####[340]####
        if (getValidationMode() == LONG_VALIDATION_MODE) //####[342]####
        {//####[342]####
            outputStream = new ExtOutputStream();//####[343]####
            streamResult = new StreamResult(outputStream);//####[344]####
            outputStream.setValidationProperties(longValidationProperties);//####[345]####
        } else {//####[346]####
            outputStream = new BaseOutputStream();//####[347]####
            streamResult = new StreamResult(outputStream);//####[348]####
            outputStream.setValidationProperties(shortValidationProperties[threadId - 1][INPUT_PAIR_num][sourceType]);//####[349]####
        }//####[350]####
        outputStream.setCurrentProp(descr);//####[352]####
        transformer.transform(source, streamResult);//####[353]####
        outputStream.checkResult(loop);//####[354]####
        if ((getValidationMode() == LONG_VALIDATION_MODE) && (outputStream instanceof ExtOutputStream)) //####[356]####
        {//####[356]####
            int threads = Launch.currentNumberBmThreads;//####[357]####
            Properties outProperties = getOutProperties(outputStream);//####[358]####
            for (int i = 0; i < threads; i++) //####[359]####
            {//####[359]####
                shortValidationProperties[i][INPUT_PAIR_num][sourceType] = (Properties) outProperties.clone();//####[360]####
            }//####[361]####
        }//####[362]####
    }//####[364]####
}//####[364]####
