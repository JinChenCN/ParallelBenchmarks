package spec.benchmarks.xml.pt.validation;//####[7]####
//####[7]####
import java.io.File;//####[9]####
import java.io.IOException;//####[10]####
import java.util.concurrent.atomic.AtomicInteger;//####[11]####
import java.util.Arrays;//####[13]####
import javax.xml.XMLConstants;//####[14]####
import javax.xml.parsers.ParserConfigurationException;//####[15]####
import javax.xml.transform.Source;//####[16]####
import javax.xml.transform.stream.StreamSource;//####[17]####
import javax.xml.validation.Schema;//####[18]####
import javax.xml.validation.SchemaFactory;//####[19]####
import javax.xml.validation.Validator;//####[20]####
import org.xml.sax.SAXException;//####[22]####
import spec.harness.Constants;//####[24]####
import spec.harness.Context;//####[25]####
import spec.harness.Launch;//####[26]####
import spec.harness.Util;//####[27]####
import spec.harness.results.BenchmarkResult;//####[28]####
import spec.io.FileCache;//####[29]####
import spec.io.FileCache.CachedFile;//####[30]####
import spec.benchmarks.xml.pt.XMLBenchmark;//####[32]####
//####[32]####
//-- ParaTask related imports//####[32]####
import pt.runtime.*;//####[32]####
import java.util.concurrent.ExecutionException;//####[32]####
import java.util.concurrent.locks.*;//####[32]####
import java.lang.reflect.*;//####[32]####
import pt.runtime.GuiThread;//####[32]####
import java.util.concurrent.BlockingQueue;//####[32]####
import java.util.ArrayList;//####[32]####
import java.util.List;//####[32]####
//####[32]####
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
//####[36]####
    private static final int XSD_NUMBER = 6;//####[36]####
//####[37]####
    private static FileCache.CachedFile[] allInstanceBytes;//####[37]####
//####[38]####
    private static FileCache.CachedFile[] allSchemaBytes;//####[38]####
//####[39]####
    private static Validator[][] allValidators;//####[39]####
//####[41]####
    public static String testType() {//####[41]####
        return MULTI;//####[42]####
    }//####[43]####
//####[44]####
    private static String[] schemaNames = { "validation_input.xsd", "periodic_table.xsd", "play.xsd", "structure.xsd", "po.xsd", "personal.xsd" };//####[44]####
//####[52]####
    private static String[] instanceNames = { "validation_input.xml", "periodicxsd.xml", "much_adoxsd.xml", "structure.xml", "po.xml", "personal.xml" };//####[52]####
//####[70]####
    private static int loops[] = { 1, 5, 3, 52, 647, 419 };//####[70]####
//####[81]####
    public static void setupBenchmark() {//####[81]####
        String dirName = Util.getProperty(Constants.XML_VALIDATION_INPUT_DIR_PROP, null);//####[82]####
        try {//####[83]####
            allInstanceBytes = new FileCache.CachedFile[XSD_NUMBER];//####[84]####
            FileCache cache = Context.getFileCache();//####[85]####
            for (int i = 0; i < XSD_NUMBER; i++) //####[86]####
            {//####[86]####
                String name = getFullName(Main.class, dirName, instanceNames[i]);//####[87]####
                allInstanceBytes[i] = cache.new CachedFile(name);//####[88]####
                allInstanceBytes[i].cache();//####[89]####
            }//####[90]####
            allSchemaBytes = new FileCache.CachedFile[XSD_NUMBER];//####[91]####
            for (int i = 0; i < XSD_NUMBER; i++) //####[92]####
            {//####[92]####
                String name = getFullName(Main.class, dirName, schemaNames[i]);//####[93]####
                allSchemaBytes[i] = cache.new CachedFile(name);//####[94]####
                allSchemaBytes[i].cache();//####[95]####
            }//####[96]####
            setupValidators(dirName);//####[98]####
        } catch (IOException e) {//####[99]####
            e.printStackTrace(Context.getOut());//####[100]####
        }//####[101]####
    }//####[102]####
//####[104]####
    private static void setupValidators(String dirName) {//####[104]####
        int threads = Launch.currentNumberBmThreads;//####[105]####
        allValidators = new Validator[threads][XSD_NUMBER];//####[106]####
        try {//####[107]####
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);//####[108]####
            sf.setErrorHandler(null);//####[109]####
            for (int i = 0; i < XSD_NUMBER; i++) //####[110]####
            {//####[110]####
                String xsdFilename = getFullName(Main.class, dirName, schemaNames[i]);//####[111]####
                File tempURI = new File(xsdFilename);//####[112]####
                Schema precompSchema;//####[113]####
                if (tempURI.isAbsolute()) //####[114]####
                {//####[114]####
                    precompSchema = sf.newSchema(new StreamSource(allSchemaBytes[i].getStream(), tempURI.toURI().toString()));//####[115]####
                } else {//####[117]####
                    precompSchema = sf.newSchema(new StreamSource(allSchemaBytes[i].getStream(), xsdFilename));//####[118]####
                }//####[119]####
                for (int j = 0; j < threads; j++) //####[120]####
                {//####[120]####
                    allValidators[j][i] = precompSchema.newValidator();//####[121]####
                }//####[122]####
            }//####[123]####
        } catch (SAXException e) {//####[124]####
            e.printStackTrace();//####[125]####
        } catch (Exception e) {//####[126]####
            e.printStackTrace();//####[127]####
        }//####[128]####
    }//####[129]####
//####[131]####
    private Validator[] schemaBoundValidator;//####[131]####
//####[133]####
    public Main(BenchmarkResult bmResult, int threadId) {//####[133]####
        super(bmResult, threadId);//####[134]####
        schemaBoundValidator = allValidators[threadId - 1];//####[135]####
    }//####[136]####
//####[138]####
    public void harnessMain() {//####[138]####
        try {//####[139]####
            long start = System.currentTimeMillis();//####[140]####
            executeWorkload();//####[141]####
            long time = System.currentTimeMillis() - start;//####[142]####
            System.out.println("Parallel xml validation has taken  " + (time / 1000.0) + " seconds.");//####[143]####
        } catch (Exception e) {//####[144]####
            e.printStackTrace(Context.getOut());//####[145]####
        }//####[146]####
    }//####[147]####
//####[149]####
    public static void main(String[] args) throws Exception {//####[149]####
        runSimple(Main.class, args);//####[150]####
    }//####[151]####
//####[153]####
    private void executeWorkload() throws ParserConfigurationException, IOException, SAXException {//####[154]####
        ParaTask.setScheduling(ParaTask.ScheduleType.MixedSchedule);//####[156]####
        ParaTask.setThreadPoolSize(ParaTask.ThreadPoolType.ONEOFF, 4);//####[157]####
        TaskIDGroup g;//####[159]####
        g = new TaskIDGroup(XSD_NUMBER);//####[160]####
        for (int i = 0; i < XSD_NUMBER; i++) //####[162]####
        {//####[162]####
            Context.getOut().println("Validating " + instanceNames[i]);//####[163]####
            TaskID id = doValidationTests(loops[i], allInstanceBytes[i], schemaBoundValidator[i]);//####[166]####
            g.add(id);//####[167]####
        }//####[168]####
        try {//####[169]####
            g.waitTillFinished();//####[170]####
        } catch (Exception e) {//####[171]####
            e.printStackTrace();//####[172]####
        }//####[173]####
    }//####[174]####
//####[176]####
    private static volatile Method __pt__doValidationTests_int_CachedFile_Validator_method = null;//####[176]####
    private synchronized static void __pt__doValidationTests_int_CachedFile_Validator_ensureMethodVarSet() {//####[176]####
        if (__pt__doValidationTests_int_CachedFile_Validator_method == null) {//####[176]####
            try {//####[176]####
                __pt__doValidationTests_int_CachedFile_Validator_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__doValidationTests", new Class[] {//####[176]####
                    int.class, CachedFile.class, Validator.class//####[176]####
                });//####[176]####
            } catch (Exception e) {//####[176]####
                e.printStackTrace();//####[176]####
            }//####[176]####
        }//####[176]####
    }//####[176]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__doValidationTests_int_CachedFile_Validator_method == null) {//####[178]####
            __pt__doValidationTests_int_CachedFile_Validator_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[178]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_Validator_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__doValidationTests_int_CachedFile_Validator_method == null) {//####[178]####
            __pt__doValidationTests_int_CachedFile_Validator_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setTaskIdArgIndexes(0);//####[178]####
        taskinfo.addDependsOn(loops);//####[178]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[178]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_Validator_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__doValidationTests_int_CachedFile_Validator_method == null) {//####[178]####
            __pt__doValidationTests_int_CachedFile_Validator_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setQueueArgIndexes(0);//####[178]####
        taskinfo.setIsPipeline(true);//####[178]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[178]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_Validator_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__doValidationTests_int_CachedFile_Validator_method == null) {//####[178]####
            __pt__doValidationTests_int_CachedFile_Validator_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setTaskIdArgIndexes(1);//####[178]####
        taskinfo.addDependsOn(file);//####[178]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[178]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_Validator_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__doValidationTests_int_CachedFile_Validator_method == null) {//####[178]####
            __pt__doValidationTests_int_CachedFile_Validator_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[178]####
        taskinfo.addDependsOn(loops);//####[178]####
        taskinfo.addDependsOn(file);//####[178]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[178]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_Validator_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__doValidationTests_int_CachedFile_Validator_method == null) {//####[178]####
            __pt__doValidationTests_int_CachedFile_Validator_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setQueueArgIndexes(0);//####[178]####
        taskinfo.setIsPipeline(true);//####[178]####
        taskinfo.setTaskIdArgIndexes(1);//####[178]####
        taskinfo.addDependsOn(file);//####[178]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[178]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_Validator_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__doValidationTests_int_CachedFile_Validator_method == null) {//####[178]####
            __pt__doValidationTests_int_CachedFile_Validator_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setQueueArgIndexes(1);//####[178]####
        taskinfo.setIsPipeline(true);//####[178]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[178]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_Validator_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__doValidationTests_int_CachedFile_Validator_method == null) {//####[178]####
            __pt__doValidationTests_int_CachedFile_Validator_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setQueueArgIndexes(1);//####[178]####
        taskinfo.setIsPipeline(true);//####[178]####
        taskinfo.setTaskIdArgIndexes(0);//####[178]####
        taskinfo.addDependsOn(loops);//####[178]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[178]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_Validator_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__doValidationTests_int_CachedFile_Validator_method == null) {//####[178]####
            __pt__doValidationTests_int_CachedFile_Validator_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setQueueArgIndexes(0, 1);//####[178]####
        taskinfo.setIsPipeline(true);//####[178]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[178]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_Validator_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__doValidationTests_int_CachedFile_Validator_method == null) {//####[178]####
            __pt__doValidationTests_int_CachedFile_Validator_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setTaskIdArgIndexes(2);//####[178]####
        taskinfo.addDependsOn(schemaValidator);//####[178]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[178]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_Validator_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__doValidationTests_int_CachedFile_Validator_method == null) {//####[178]####
            __pt__doValidationTests_int_CachedFile_Validator_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[178]####
        taskinfo.addDependsOn(loops);//####[178]####
        taskinfo.addDependsOn(schemaValidator);//####[178]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[178]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_Validator_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__doValidationTests_int_CachedFile_Validator_method == null) {//####[178]####
            __pt__doValidationTests_int_CachedFile_Validator_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setQueueArgIndexes(0);//####[178]####
        taskinfo.setIsPipeline(true);//####[178]####
        taskinfo.setTaskIdArgIndexes(2);//####[178]####
        taskinfo.addDependsOn(schemaValidator);//####[178]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[178]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_Validator_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__doValidationTests_int_CachedFile_Validator_method == null) {//####[178]####
            __pt__doValidationTests_int_CachedFile_Validator_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[178]####
        taskinfo.addDependsOn(file);//####[178]####
        taskinfo.addDependsOn(schemaValidator);//####[178]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[178]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_Validator_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__doValidationTests_int_CachedFile_Validator_method == null) {//####[178]####
            __pt__doValidationTests_int_CachedFile_Validator_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setTaskIdArgIndexes(0, 1, 2);//####[178]####
        taskinfo.addDependsOn(loops);//####[178]####
        taskinfo.addDependsOn(file);//####[178]####
        taskinfo.addDependsOn(schemaValidator);//####[178]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[178]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_Validator_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__doValidationTests_int_CachedFile_Validator_method == null) {//####[178]####
            __pt__doValidationTests_int_CachedFile_Validator_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setQueueArgIndexes(0);//####[178]####
        taskinfo.setIsPipeline(true);//####[178]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[178]####
        taskinfo.addDependsOn(file);//####[178]####
        taskinfo.addDependsOn(schemaValidator);//####[178]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[178]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_Validator_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__doValidationTests_int_CachedFile_Validator_method == null) {//####[178]####
            __pt__doValidationTests_int_CachedFile_Validator_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setQueueArgIndexes(1);//####[178]####
        taskinfo.setIsPipeline(true);//####[178]####
        taskinfo.setTaskIdArgIndexes(2);//####[178]####
        taskinfo.addDependsOn(schemaValidator);//####[178]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[178]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_Validator_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__doValidationTests_int_CachedFile_Validator_method == null) {//####[178]####
            __pt__doValidationTests_int_CachedFile_Validator_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setQueueArgIndexes(1);//####[178]####
        taskinfo.setIsPipeline(true);//####[178]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[178]####
        taskinfo.addDependsOn(loops);//####[178]####
        taskinfo.addDependsOn(schemaValidator);//####[178]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[178]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_Validator_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__doValidationTests_int_CachedFile_Validator_method == null) {//####[178]####
            __pt__doValidationTests_int_CachedFile_Validator_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setQueueArgIndexes(0, 1);//####[178]####
        taskinfo.setIsPipeline(true);//####[178]####
        taskinfo.setTaskIdArgIndexes(2);//####[178]####
        taskinfo.addDependsOn(schemaValidator);//####[178]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[178]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_Validator_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__doValidationTests_int_CachedFile_Validator_method == null) {//####[178]####
            __pt__doValidationTests_int_CachedFile_Validator_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setQueueArgIndexes(2);//####[178]####
        taskinfo.setIsPipeline(true);//####[178]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[178]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_Validator_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__doValidationTests_int_CachedFile_Validator_method == null) {//####[178]####
            __pt__doValidationTests_int_CachedFile_Validator_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setQueueArgIndexes(2);//####[178]####
        taskinfo.setIsPipeline(true);//####[178]####
        taskinfo.setTaskIdArgIndexes(0);//####[178]####
        taskinfo.addDependsOn(loops);//####[178]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[178]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_Validator_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__doValidationTests_int_CachedFile_Validator_method == null) {//####[178]####
            __pt__doValidationTests_int_CachedFile_Validator_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setQueueArgIndexes(0, 2);//####[178]####
        taskinfo.setIsPipeline(true);//####[178]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[178]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_Validator_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__doValidationTests_int_CachedFile_Validator_method == null) {//####[178]####
            __pt__doValidationTests_int_CachedFile_Validator_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setQueueArgIndexes(2);//####[178]####
        taskinfo.setIsPipeline(true);//####[178]####
        taskinfo.setTaskIdArgIndexes(1);//####[178]####
        taskinfo.addDependsOn(file);//####[178]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[178]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_Validator_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__doValidationTests_int_CachedFile_Validator_method == null) {//####[178]####
            __pt__doValidationTests_int_CachedFile_Validator_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setQueueArgIndexes(2);//####[178]####
        taskinfo.setIsPipeline(true);//####[178]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[178]####
        taskinfo.addDependsOn(loops);//####[178]####
        taskinfo.addDependsOn(file);//####[178]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[178]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_Validator_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__doValidationTests_int_CachedFile_Validator_method == null) {//####[178]####
            __pt__doValidationTests_int_CachedFile_Validator_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setQueueArgIndexes(0, 2);//####[178]####
        taskinfo.setIsPipeline(true);//####[178]####
        taskinfo.setTaskIdArgIndexes(1);//####[178]####
        taskinfo.addDependsOn(file);//####[178]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[178]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_Validator_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__doValidationTests_int_CachedFile_Validator_method == null) {//####[178]####
            __pt__doValidationTests_int_CachedFile_Validator_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setQueueArgIndexes(1, 2);//####[178]####
        taskinfo.setIsPipeline(true);//####[178]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[178]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_Validator_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__doValidationTests_int_CachedFile_Validator_method == null) {//####[178]####
            __pt__doValidationTests_int_CachedFile_Validator_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setQueueArgIndexes(1, 2);//####[178]####
        taskinfo.setIsPipeline(true);//####[178]####
        taskinfo.setTaskIdArgIndexes(0);//####[178]####
        taskinfo.addDependsOn(loops);//####[178]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[178]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_Validator_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[178]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[178]####
    }//####[178]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        // ensure Method variable is set//####[178]####
        if (__pt__doValidationTests_int_CachedFile_Validator_method == null) {//####[178]####
            __pt__doValidationTests_int_CachedFile_Validator_ensureMethodVarSet();//####[178]####
        }//####[178]####
        taskinfo.setQueueArgIndexes(0, 1, 2);//####[178]####
        taskinfo.setIsPipeline(true);//####[178]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[178]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_Validator_method);//####[178]####
        taskinfo.setInstance(this);//####[178]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[178]####
    }//####[178]####
    public void __pt__doValidationTests(int loops, CachedFile file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[178]####
        for (int i = loops - 1; i >= 0; i--) //####[180]####
        {//####[180]####
            validateSource(i, createDomSource(file), schemaValidator);//####[181]####
            validateSource(i, createSaxSource(file), schemaValidator);//####[182]####
        }//####[183]####
    }//####[185]####
//####[185]####
//####[191]####
    private void validateSource(int loop, Source source, Validator schemaValidator) {//####[191]####
        schemaValidator.reset();//####[192]####
        schemaValidator.setErrorHandler(null);//####[193]####
        try {//####[195]####
            schemaValidator.validate(source);//####[197]####
            if (loop == 0) //####[199]####
            {//####[199]####
                Context.getOut().print("\tas " + source.getClass().getName());//####[200]####
                Context.getOut().println(" succeeded. (correct result)");//####[201]####
            }//####[202]####
        } catch (SAXException e) {//####[203]####
            Context.getOut().print("\tas " + source.getClass().getName());//####[204]####
            Context.getOut().println(" failed. (Incorrect result)" + Arrays.toString(loops));//####[205]####
            e.printStackTrace(Context.getOut());//####[206]####
        } catch (IOException e) {//####[207]####
            Context.getOut().println("Unable to validate due to IOException.");//####[208]####
        }//####[209]####
    }//####[210]####
}//####[210]####
