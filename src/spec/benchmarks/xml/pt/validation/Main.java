package spec.benchmarks.xml.pt.validation;//####[7]####
//####[7]####
import java.io.File;//####[9]####
import java.io.IOException;//####[10]####
import java.util.Arrays;//####[12]####
import javax.xml.XMLConstants;//####[13]####
import javax.xml.parsers.ParserConfigurationException;//####[14]####
import javax.xml.transform.Source;//####[15]####
import javax.xml.transform.stream.StreamSource;//####[16]####
import javax.xml.validation.Schema;//####[17]####
import javax.xml.validation.SchemaFactory;//####[18]####
import javax.xml.validation.Validator;//####[19]####
import org.xml.sax.SAXException;//####[21]####
import spec.harness.Constants;//####[23]####
import spec.harness.Context;//####[24]####
import spec.harness.Launch;//####[25]####
import spec.harness.Util;//####[26]####
import spec.harness.results.BenchmarkResult;//####[27]####
import spec.io.FileCache;//####[28]####
import spec.io.FileCache.CachedFile;//####[29]####
import spec.benchmarks.xml.pt.XMLBenchmark;//####[31]####
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
public class Main extends XMLBenchmark {//####[33]####
    static{ParaTask.init();}//####[33]####
    /*  ParaTask helper method to access private/protected slots *///####[33]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[33]####
        if (m.getParameterTypes().length == 0)//####[33]####
            m.invoke(instance);//####[33]####
        else if ((m.getParameterTypes().length == 1))//####[33]####
            m.invoke(instance, arg);//####[33]####
        else //####[33]####
            m.invoke(instance, arg, interResult);//####[33]####
    }//####[33]####
//####[35]####
    private static final int XSD_NUMBER = 6;//####[35]####
//####[36]####
    private static FileCache.CachedFile[] allInstanceBytes;//####[36]####
//####[37]####
    private static FileCache.CachedFile[] allSchemaBytes;//####[37]####
//####[38]####
    private static Validator[][][] allValidators;//####[38]####
//####[39]####
    private static int THREADSNUM = 6;//####[39]####
//####[40]####
    private static ParaTask.ScheduleType scheduleType = ParaTask.ScheduleType.MixedSchedule;//####[40]####
//####[42]####
    public static String testType() {//####[42]####
        return MULTI;//####[43]####
    }//####[44]####
//####[45]####
    private static String[] schemaNames = { "validation_input.xsd", "periodic_table.xsd", "play.xsd", "structure.xsd", "po.xsd", "personal.xsd" };//####[45]####
//####[53]####
    private static String[] instanceNames = { "validation_input.xml", "periodicxsd.xml", "much_adoxsd.xml", "structure.xml", "po.xml", "personal.xml" };//####[53]####
//####[71]####
    private static int loops[] = { 1, 5, 3, 52, 647, 419 };//####[71]####
//####[82]####
    public static void setupBenchmark() {//####[82]####
        String dirName = Util.getProperty(Constants.XML_VALIDATION_INPUT_DIR_PROP, null);//####[83]####
        try {//####[84]####
            allInstanceBytes = new FileCache.CachedFile[XSD_NUMBER];//####[85]####
            FileCache cache = Context.getFileCache();//####[86]####
            for (int i = 0; i < XSD_NUMBER; i++) //####[87]####
            {//####[87]####
                String name = getFullName(Main.class, dirName, instanceNames[i]);//####[88]####
                allInstanceBytes[i] = cache.new CachedFile(name);//####[89]####
                allInstanceBytes[i].cache();//####[90]####
            }//####[91]####
            allSchemaBytes = new FileCache.CachedFile[XSD_NUMBER];//####[92]####
            for (int i = 0; i < XSD_NUMBER; i++) //####[93]####
            {//####[93]####
                String name = getFullName(Main.class, dirName, schemaNames[i]);//####[94]####
                allSchemaBytes[i] = cache.new CachedFile(name);//####[95]####
                allSchemaBytes[i].cache();//####[96]####
            }//####[97]####
            setupValidators(dirName);//####[99]####
        } catch (IOException e) {//####[100]####
            e.printStackTrace(Context.getOut());//####[101]####
        }//####[102]####
    }//####[103]####
//####[105]####
    private static void setupValidators(String dirName) {//####[105]####
        int threads = Launch.currentNumberBmThreads;//####[106]####
        allValidators = new Validator[threads][XSD_NUMBER][];//####[107]####
        try {//####[108]####
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);//####[109]####
            sf.setErrorHandler(null);//####[110]####
            for (int i = 0; i < XSD_NUMBER; i++) //####[111]####
            {//####[111]####
                String xsdFilename = getFullName(Main.class, dirName, schemaNames[i]);//####[112]####
                File tempURI = new File(xsdFilename);//####[113]####
                Schema precompSchema;//####[114]####
                if (tempURI.isAbsolute()) //####[115]####
                {//####[115]####
                    precompSchema = sf.newSchema(new StreamSource(allSchemaBytes[i].getStream(), tempURI.toURI().toString()));//####[116]####
                } else {//####[118]####
                    precompSchema = sf.newSchema(new StreamSource(allSchemaBytes[i].getStream(), xsdFilename));//####[119]####
                }//####[120]####
                for (int j = 0; j < threads; j++) //####[121]####
                {//####[121]####
                    Validator[] validatorForLoops = new Validator[THREADSNUM];//####[122]####
                    for (int k = 0; k < THREADSNUM; k++) //####[123]####
                    {//####[123]####
                        validatorForLoops[k] = precompSchema.newValidator();//####[124]####
                    }//####[125]####
                    allValidators[j][i] = validatorForLoops;//####[126]####
                }//####[127]####
            }//####[128]####
        } catch (SAXException e) {//####[129]####
            e.printStackTrace();//####[130]####
        } catch (Exception e) {//####[131]####
            e.printStackTrace();//####[132]####
        }//####[133]####
    }//####[134]####
//####[136]####
    private Validator[][] schemaBoundValidator;//####[136]####
//####[138]####
    public Main(BenchmarkResult bmResult, int threadId) {//####[138]####
        super(bmResult, threadId);//####[139]####
        schemaBoundValidator = allValidators[threadId - 1];//####[140]####
    }//####[141]####
//####[143]####
    public void harnessMain() {//####[143]####
        try {//####[144]####
            long start = System.currentTimeMillis();//####[145]####
            executeWorkload();//####[146]####
            long time = System.currentTimeMillis() - start;//####[147]####
            System.out.println("Parallel xml validation has taken  " + (time / 1000.0) + " seconds.");//####[148]####
        } catch (Exception e) {//####[149]####
            e.printStackTrace(Context.getOut());//####[150]####
        }//####[151]####
    }//####[152]####
//####[154]####
    public static void main(String[] args) throws Exception {//####[154]####
        runSimple(Main.class, args);//####[155]####
    }//####[156]####
//####[158]####
    private void executeWorkload() throws ParserConfigurationException, IOException, SAXException {//####[159]####
        ParaTask.setScheduling(scheduleType);//####[161]####
        ParaTask.setThreadPoolSize(ParaTask.ThreadPoolType.ONEOFF, THREADSNUM);//####[162]####
        TaskIDGroup g;//####[164]####
        g = new TaskIDGroup(XSD_NUMBER);//####[165]####
        for (int i = 0; i < XSD_NUMBER; i++) //####[167]####
        {//####[167]####
            Context.getOut().println("Validating " + instanceNames[i]);//####[168]####
            TaskID id = doValidationTests(loops[i], allInstanceBytes[i], schemaBoundValidator[i]);//####[171]####
            g.add(id);//####[172]####
        }//####[173]####
        try {//####[174]####
            g.waitTillFinished();//####[175]####
        } catch (Exception e) {//####[176]####
            e.printStackTrace();//####[177]####
        }//####[178]####
    }//####[179]####
//####[181]####
    private static volatile Method __pt__doValidationTests_int_CachedFile_ValidatorAr_method = null;//####[181]####
    private synchronized static void __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet() {//####[181]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[181]####
            try {//####[181]####
                __pt__doValidationTests_int_CachedFile_ValidatorAr_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__doValidationTests", new Class[] {//####[181]####
                    int.class, CachedFile.class, Validator[].class//####[181]####
                });//####[181]####
            } catch (Exception e) {//####[181]####
                e.printStackTrace();//####[181]####
            }//####[181]####
        }//####[181]####
    }//####[181]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[183]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        // ensure Method variable is set//####[183]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[183]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[183]####
        }//####[183]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[183]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[183]####
        taskinfo.setInstance(this);//####[183]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[183]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        // ensure Method variable is set//####[183]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[183]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[183]####
        }//####[183]####
        taskinfo.setTaskIdArgIndexes(0);//####[183]####
        taskinfo.addDependsOn(loops);//####[183]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[183]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[183]####
        taskinfo.setInstance(this);//####[183]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[183]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        // ensure Method variable is set//####[183]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[183]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[183]####
        }//####[183]####
        taskinfo.setQueueArgIndexes(0);//####[183]####
        taskinfo.setIsPipeline(true);//####[183]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[183]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[183]####
        taskinfo.setInstance(this);//####[183]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[183]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        // ensure Method variable is set//####[183]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[183]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[183]####
        }//####[183]####
        taskinfo.setTaskIdArgIndexes(1);//####[183]####
        taskinfo.addDependsOn(file);//####[183]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[183]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[183]####
        taskinfo.setInstance(this);//####[183]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[183]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        // ensure Method variable is set//####[183]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[183]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[183]####
        }//####[183]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[183]####
        taskinfo.addDependsOn(loops);//####[183]####
        taskinfo.addDependsOn(file);//####[183]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[183]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[183]####
        taskinfo.setInstance(this);//####[183]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[183]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        // ensure Method variable is set//####[183]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[183]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[183]####
        }//####[183]####
        taskinfo.setQueueArgIndexes(0);//####[183]####
        taskinfo.setIsPipeline(true);//####[183]####
        taskinfo.setTaskIdArgIndexes(1);//####[183]####
        taskinfo.addDependsOn(file);//####[183]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[183]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[183]####
        taskinfo.setInstance(this);//####[183]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[183]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        // ensure Method variable is set//####[183]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[183]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[183]####
        }//####[183]####
        taskinfo.setQueueArgIndexes(1);//####[183]####
        taskinfo.setIsPipeline(true);//####[183]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[183]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[183]####
        taskinfo.setInstance(this);//####[183]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[183]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        // ensure Method variable is set//####[183]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[183]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[183]####
        }//####[183]####
        taskinfo.setQueueArgIndexes(1);//####[183]####
        taskinfo.setIsPipeline(true);//####[183]####
        taskinfo.setTaskIdArgIndexes(0);//####[183]####
        taskinfo.addDependsOn(loops);//####[183]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[183]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[183]####
        taskinfo.setInstance(this);//####[183]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[183]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        // ensure Method variable is set//####[183]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[183]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[183]####
        }//####[183]####
        taskinfo.setQueueArgIndexes(0, 1);//####[183]####
        taskinfo.setIsPipeline(true);//####[183]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[183]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[183]####
        taskinfo.setInstance(this);//####[183]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[183]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        // ensure Method variable is set//####[183]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[183]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[183]####
        }//####[183]####
        taskinfo.setTaskIdArgIndexes(2);//####[183]####
        taskinfo.addDependsOn(schemaValidator);//####[183]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[183]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[183]####
        taskinfo.setInstance(this);//####[183]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[183]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        // ensure Method variable is set//####[183]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[183]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[183]####
        }//####[183]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[183]####
        taskinfo.addDependsOn(loops);//####[183]####
        taskinfo.addDependsOn(schemaValidator);//####[183]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[183]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[183]####
        taskinfo.setInstance(this);//####[183]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[183]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        // ensure Method variable is set//####[183]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[183]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[183]####
        }//####[183]####
        taskinfo.setQueueArgIndexes(0);//####[183]####
        taskinfo.setIsPipeline(true);//####[183]####
        taskinfo.setTaskIdArgIndexes(2);//####[183]####
        taskinfo.addDependsOn(schemaValidator);//####[183]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[183]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[183]####
        taskinfo.setInstance(this);//####[183]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[183]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        // ensure Method variable is set//####[183]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[183]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[183]####
        }//####[183]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[183]####
        taskinfo.addDependsOn(file);//####[183]####
        taskinfo.addDependsOn(schemaValidator);//####[183]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[183]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[183]####
        taskinfo.setInstance(this);//####[183]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[183]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        // ensure Method variable is set//####[183]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[183]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[183]####
        }//####[183]####
        taskinfo.setTaskIdArgIndexes(0, 1, 2);//####[183]####
        taskinfo.addDependsOn(loops);//####[183]####
        taskinfo.addDependsOn(file);//####[183]####
        taskinfo.addDependsOn(schemaValidator);//####[183]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[183]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[183]####
        taskinfo.setInstance(this);//####[183]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[183]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        // ensure Method variable is set//####[183]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[183]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[183]####
        }//####[183]####
        taskinfo.setQueueArgIndexes(0);//####[183]####
        taskinfo.setIsPipeline(true);//####[183]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[183]####
        taskinfo.addDependsOn(file);//####[183]####
        taskinfo.addDependsOn(schemaValidator);//####[183]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[183]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[183]####
        taskinfo.setInstance(this);//####[183]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[183]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        // ensure Method variable is set//####[183]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[183]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[183]####
        }//####[183]####
        taskinfo.setQueueArgIndexes(1);//####[183]####
        taskinfo.setIsPipeline(true);//####[183]####
        taskinfo.setTaskIdArgIndexes(2);//####[183]####
        taskinfo.addDependsOn(schemaValidator);//####[183]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[183]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[183]####
        taskinfo.setInstance(this);//####[183]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[183]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        // ensure Method variable is set//####[183]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[183]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[183]####
        }//####[183]####
        taskinfo.setQueueArgIndexes(1);//####[183]####
        taskinfo.setIsPipeline(true);//####[183]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[183]####
        taskinfo.addDependsOn(loops);//####[183]####
        taskinfo.addDependsOn(schemaValidator);//####[183]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[183]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[183]####
        taskinfo.setInstance(this);//####[183]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[183]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        // ensure Method variable is set//####[183]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[183]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[183]####
        }//####[183]####
        taskinfo.setQueueArgIndexes(0, 1);//####[183]####
        taskinfo.setIsPipeline(true);//####[183]####
        taskinfo.setTaskIdArgIndexes(2);//####[183]####
        taskinfo.addDependsOn(schemaValidator);//####[183]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[183]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[183]####
        taskinfo.setInstance(this);//####[183]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[183]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        // ensure Method variable is set//####[183]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[183]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[183]####
        }//####[183]####
        taskinfo.setQueueArgIndexes(2);//####[183]####
        taskinfo.setIsPipeline(true);//####[183]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[183]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[183]####
        taskinfo.setInstance(this);//####[183]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[183]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        // ensure Method variable is set//####[183]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[183]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[183]####
        }//####[183]####
        taskinfo.setQueueArgIndexes(2);//####[183]####
        taskinfo.setIsPipeline(true);//####[183]####
        taskinfo.setTaskIdArgIndexes(0);//####[183]####
        taskinfo.addDependsOn(loops);//####[183]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[183]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[183]####
        taskinfo.setInstance(this);//####[183]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[183]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        // ensure Method variable is set//####[183]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[183]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[183]####
        }//####[183]####
        taskinfo.setQueueArgIndexes(0, 2);//####[183]####
        taskinfo.setIsPipeline(true);//####[183]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[183]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[183]####
        taskinfo.setInstance(this);//####[183]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[183]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        // ensure Method variable is set//####[183]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[183]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[183]####
        }//####[183]####
        taskinfo.setQueueArgIndexes(2);//####[183]####
        taskinfo.setIsPipeline(true);//####[183]####
        taskinfo.setTaskIdArgIndexes(1);//####[183]####
        taskinfo.addDependsOn(file);//####[183]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[183]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[183]####
        taskinfo.setInstance(this);//####[183]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[183]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        // ensure Method variable is set//####[183]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[183]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[183]####
        }//####[183]####
        taskinfo.setQueueArgIndexes(2);//####[183]####
        taskinfo.setIsPipeline(true);//####[183]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[183]####
        taskinfo.addDependsOn(loops);//####[183]####
        taskinfo.addDependsOn(file);//####[183]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[183]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[183]####
        taskinfo.setInstance(this);//####[183]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[183]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        // ensure Method variable is set//####[183]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[183]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[183]####
        }//####[183]####
        taskinfo.setQueueArgIndexes(0, 2);//####[183]####
        taskinfo.setIsPipeline(true);//####[183]####
        taskinfo.setTaskIdArgIndexes(1);//####[183]####
        taskinfo.addDependsOn(file);//####[183]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[183]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[183]####
        taskinfo.setInstance(this);//####[183]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[183]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        // ensure Method variable is set//####[183]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[183]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[183]####
        }//####[183]####
        taskinfo.setQueueArgIndexes(1, 2);//####[183]####
        taskinfo.setIsPipeline(true);//####[183]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[183]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[183]####
        taskinfo.setInstance(this);//####[183]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[183]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        // ensure Method variable is set//####[183]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[183]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[183]####
        }//####[183]####
        taskinfo.setQueueArgIndexes(1, 2);//####[183]####
        taskinfo.setIsPipeline(true);//####[183]####
        taskinfo.setTaskIdArgIndexes(0);//####[183]####
        taskinfo.addDependsOn(loops);//####[183]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[183]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[183]####
        taskinfo.setInstance(this);//####[183]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[183]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[183]####
    }//####[183]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        // ensure Method variable is set//####[183]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[183]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[183]####
        }//####[183]####
        taskinfo.setQueueArgIndexes(0, 1, 2);//####[183]####
        taskinfo.setIsPipeline(true);//####[183]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[183]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[183]####
        taskinfo.setInstance(this);//####[183]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[183]####
    }//####[183]####
    public void __pt__doValidationTests(int loops, CachedFile file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[183]####
        if (loops < THREADSNUM) //####[184]####
        {//####[185]####
            for (int i = loops - 1; i >= 0; i--) //####[186]####
            {//####[186]####
                validateSource(i, createDomSource(file), schemaValidator[i]);//####[187]####
                validateSource(i, createSaxSource(file), schemaValidator[i]);//####[188]####
            }//####[189]####
        } else {//####[190]####
            TaskIDGroup g;//####[191]####
            g = new TaskIDGroup(THREADSNUM);//####[192]####
            int[] loopsForThread = new int[THREADSNUM];//####[193]####
            for (int i = 0; i < THREADSNUM - 1; i++) //####[194]####
            {//####[195]####
                loopsForThread[i] = loops / THREADSNUM;//####[196]####
            }//####[197]####
            loopsForThread[THREADSNUM - 1] = loops - (THREADSNUM - 1) * loops / THREADSNUM;//####[198]####
            for (int j = 0; j < THREADSNUM; j++) //####[200]####
            {//####[201]####
                TaskID id = doValidationLoop(loopsForThread[j], file, schemaValidator[j]);//####[202]####
                g.add(id);//####[203]####
            }//####[204]####
            try {//####[211]####
                g.waitTillFinished();//####[212]####
            } catch (Exception e) {//####[213]####
                e.printStackTrace();//####[214]####
            }//####[215]####
        }//####[216]####
    }//####[218]####
//####[218]####
//####[220]####
    private static volatile Method __pt__doValidationLoop_int_CachedFile_Validator_method = null;//####[220]####
    private synchronized static void __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet() {//####[220]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[220]####
            try {//####[220]####
                __pt__doValidationLoop_int_CachedFile_Validator_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__doValidationLoop", new Class[] {//####[220]####
                    int.class, CachedFile.class, Validator.class//####[220]####
                });//####[220]####
            } catch (Exception e) {//####[220]####
                e.printStackTrace();//####[220]####
            }//####[220]####
        }//####[220]####
    }//####[220]####
    private TaskID<Void> doValidationLoop(int loop, CachedFile file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[221]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(int loop, CachedFile file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        // ensure Method variable is set//####[221]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[221]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[221]####
        }//####[221]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[221]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[221]####
        taskinfo.setInstance(this);//####[221]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, CachedFile file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[221]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, CachedFile file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        // ensure Method variable is set//####[221]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[221]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[221]####
        }//####[221]####
        taskinfo.setTaskIdArgIndexes(0);//####[221]####
        taskinfo.addDependsOn(loop);//####[221]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[221]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[221]####
        taskinfo.setInstance(this);//####[221]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, CachedFile file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[221]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, CachedFile file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        // ensure Method variable is set//####[221]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[221]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[221]####
        }//####[221]####
        taskinfo.setQueueArgIndexes(0);//####[221]####
        taskinfo.setIsPipeline(true);//####[221]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[221]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[221]####
        taskinfo.setInstance(this);//####[221]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(int loop, TaskID<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[221]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(int loop, TaskID<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        // ensure Method variable is set//####[221]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[221]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[221]####
        }//####[221]####
        taskinfo.setTaskIdArgIndexes(1);//####[221]####
        taskinfo.addDependsOn(file);//####[221]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[221]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[221]####
        taskinfo.setInstance(this);//####[221]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, TaskID<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[221]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, TaskID<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        // ensure Method variable is set//####[221]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[221]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[221]####
        }//####[221]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[221]####
        taskinfo.addDependsOn(loop);//####[221]####
        taskinfo.addDependsOn(file);//####[221]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[221]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[221]####
        taskinfo.setInstance(this);//####[221]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, TaskID<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[221]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, TaskID<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        // ensure Method variable is set//####[221]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[221]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[221]####
        }//####[221]####
        taskinfo.setQueueArgIndexes(0);//####[221]####
        taskinfo.setIsPipeline(true);//####[221]####
        taskinfo.setTaskIdArgIndexes(1);//####[221]####
        taskinfo.addDependsOn(file);//####[221]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[221]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[221]####
        taskinfo.setInstance(this);//####[221]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(int loop, BlockingQueue<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[221]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(int loop, BlockingQueue<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        // ensure Method variable is set//####[221]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[221]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[221]####
        }//####[221]####
        taskinfo.setQueueArgIndexes(1);//####[221]####
        taskinfo.setIsPipeline(true);//####[221]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[221]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[221]####
        taskinfo.setInstance(this);//####[221]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, BlockingQueue<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[221]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, BlockingQueue<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        // ensure Method variable is set//####[221]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[221]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[221]####
        }//####[221]####
        taskinfo.setQueueArgIndexes(1);//####[221]####
        taskinfo.setIsPipeline(true);//####[221]####
        taskinfo.setTaskIdArgIndexes(0);//####[221]####
        taskinfo.addDependsOn(loop);//####[221]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[221]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[221]####
        taskinfo.setInstance(this);//####[221]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, BlockingQueue<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[221]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, BlockingQueue<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        // ensure Method variable is set//####[221]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[221]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[221]####
        }//####[221]####
        taskinfo.setQueueArgIndexes(0, 1);//####[221]####
        taskinfo.setIsPipeline(true);//####[221]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[221]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[221]####
        taskinfo.setInstance(this);//####[221]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(int loop, CachedFile file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[221]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(int loop, CachedFile file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        // ensure Method variable is set//####[221]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[221]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[221]####
        }//####[221]####
        taskinfo.setTaskIdArgIndexes(2);//####[221]####
        taskinfo.addDependsOn(schemaValidator);//####[221]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[221]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[221]####
        taskinfo.setInstance(this);//####[221]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, CachedFile file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[221]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, CachedFile file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        // ensure Method variable is set//####[221]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[221]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[221]####
        }//####[221]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[221]####
        taskinfo.addDependsOn(loop);//####[221]####
        taskinfo.addDependsOn(schemaValidator);//####[221]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[221]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[221]####
        taskinfo.setInstance(this);//####[221]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, CachedFile file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[221]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, CachedFile file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        // ensure Method variable is set//####[221]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[221]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[221]####
        }//####[221]####
        taskinfo.setQueueArgIndexes(0);//####[221]####
        taskinfo.setIsPipeline(true);//####[221]####
        taskinfo.setTaskIdArgIndexes(2);//####[221]####
        taskinfo.addDependsOn(schemaValidator);//####[221]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[221]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[221]####
        taskinfo.setInstance(this);//####[221]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(int loop, TaskID<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[221]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(int loop, TaskID<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        // ensure Method variable is set//####[221]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[221]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[221]####
        }//####[221]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[221]####
        taskinfo.addDependsOn(file);//####[221]####
        taskinfo.addDependsOn(schemaValidator);//####[221]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[221]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[221]####
        taskinfo.setInstance(this);//####[221]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, TaskID<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[221]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, TaskID<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        // ensure Method variable is set//####[221]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[221]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[221]####
        }//####[221]####
        taskinfo.setTaskIdArgIndexes(0, 1, 2);//####[221]####
        taskinfo.addDependsOn(loop);//####[221]####
        taskinfo.addDependsOn(file);//####[221]####
        taskinfo.addDependsOn(schemaValidator);//####[221]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[221]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[221]####
        taskinfo.setInstance(this);//####[221]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, TaskID<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[221]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, TaskID<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        // ensure Method variable is set//####[221]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[221]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[221]####
        }//####[221]####
        taskinfo.setQueueArgIndexes(0);//####[221]####
        taskinfo.setIsPipeline(true);//####[221]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[221]####
        taskinfo.addDependsOn(file);//####[221]####
        taskinfo.addDependsOn(schemaValidator);//####[221]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[221]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[221]####
        taskinfo.setInstance(this);//####[221]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(int loop, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[221]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(int loop, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        // ensure Method variable is set//####[221]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[221]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[221]####
        }//####[221]####
        taskinfo.setQueueArgIndexes(1);//####[221]####
        taskinfo.setIsPipeline(true);//####[221]####
        taskinfo.setTaskIdArgIndexes(2);//####[221]####
        taskinfo.addDependsOn(schemaValidator);//####[221]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[221]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[221]####
        taskinfo.setInstance(this);//####[221]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[221]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        // ensure Method variable is set//####[221]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[221]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[221]####
        }//####[221]####
        taskinfo.setQueueArgIndexes(1);//####[221]####
        taskinfo.setIsPipeline(true);//####[221]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[221]####
        taskinfo.addDependsOn(loop);//####[221]####
        taskinfo.addDependsOn(schemaValidator);//####[221]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[221]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[221]####
        taskinfo.setInstance(this);//####[221]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[221]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        // ensure Method variable is set//####[221]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[221]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[221]####
        }//####[221]####
        taskinfo.setQueueArgIndexes(0, 1);//####[221]####
        taskinfo.setIsPipeline(true);//####[221]####
        taskinfo.setTaskIdArgIndexes(2);//####[221]####
        taskinfo.addDependsOn(schemaValidator);//####[221]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[221]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[221]####
        taskinfo.setInstance(this);//####[221]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(int loop, CachedFile file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[221]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(int loop, CachedFile file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        // ensure Method variable is set//####[221]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[221]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[221]####
        }//####[221]####
        taskinfo.setQueueArgIndexes(2);//####[221]####
        taskinfo.setIsPipeline(true);//####[221]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[221]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[221]####
        taskinfo.setInstance(this);//####[221]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, CachedFile file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[221]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, CachedFile file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        // ensure Method variable is set//####[221]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[221]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[221]####
        }//####[221]####
        taskinfo.setQueueArgIndexes(2);//####[221]####
        taskinfo.setIsPipeline(true);//####[221]####
        taskinfo.setTaskIdArgIndexes(0);//####[221]####
        taskinfo.addDependsOn(loop);//####[221]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[221]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[221]####
        taskinfo.setInstance(this);//####[221]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, CachedFile file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[221]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, CachedFile file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        // ensure Method variable is set//####[221]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[221]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[221]####
        }//####[221]####
        taskinfo.setQueueArgIndexes(0, 2);//####[221]####
        taskinfo.setIsPipeline(true);//####[221]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[221]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[221]####
        taskinfo.setInstance(this);//####[221]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(int loop, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[221]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(int loop, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        // ensure Method variable is set//####[221]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[221]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[221]####
        }//####[221]####
        taskinfo.setQueueArgIndexes(2);//####[221]####
        taskinfo.setIsPipeline(true);//####[221]####
        taskinfo.setTaskIdArgIndexes(1);//####[221]####
        taskinfo.addDependsOn(file);//####[221]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[221]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[221]####
        taskinfo.setInstance(this);//####[221]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[221]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        // ensure Method variable is set//####[221]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[221]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[221]####
        }//####[221]####
        taskinfo.setQueueArgIndexes(2);//####[221]####
        taskinfo.setIsPipeline(true);//####[221]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[221]####
        taskinfo.addDependsOn(loop);//####[221]####
        taskinfo.addDependsOn(file);//####[221]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[221]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[221]####
        taskinfo.setInstance(this);//####[221]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[221]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        // ensure Method variable is set//####[221]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[221]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[221]####
        }//####[221]####
        taskinfo.setQueueArgIndexes(0, 2);//####[221]####
        taskinfo.setIsPipeline(true);//####[221]####
        taskinfo.setTaskIdArgIndexes(1);//####[221]####
        taskinfo.addDependsOn(file);//####[221]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[221]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[221]####
        taskinfo.setInstance(this);//####[221]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(int loop, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[221]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(int loop, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        // ensure Method variable is set//####[221]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[221]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[221]####
        }//####[221]####
        taskinfo.setQueueArgIndexes(1, 2);//####[221]####
        taskinfo.setIsPipeline(true);//####[221]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[221]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[221]####
        taskinfo.setInstance(this);//####[221]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[221]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        // ensure Method variable is set//####[221]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[221]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[221]####
        }//####[221]####
        taskinfo.setQueueArgIndexes(1, 2);//####[221]####
        taskinfo.setIsPipeline(true);//####[221]####
        taskinfo.setTaskIdArgIndexes(0);//####[221]####
        taskinfo.addDependsOn(loop);//####[221]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[221]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[221]####
        taskinfo.setInstance(this);//####[221]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[221]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        // ensure Method variable is set//####[221]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[221]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[221]####
        }//####[221]####
        taskinfo.setQueueArgIndexes(0, 1, 2);//####[221]####
        taskinfo.setIsPipeline(true);//####[221]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[221]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[221]####
        taskinfo.setInstance(this);//####[221]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[221]####
    }//####[221]####
    public void __pt__doValidationLoop(int loop, CachedFile file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[221]####
        for (int i = loop - 1; i >= 0; i--) //####[222]####
        {//####[222]####
            validateSource(i, createDomSource(file), schemaValidator);//####[223]####
            validateSource(i, createSaxSource(file), schemaValidator);//####[224]####
        }//####[225]####
    }//####[228]####
//####[228]####
//####[234]####
    private void validateSource(int loop, Source source, Validator schemaValidator) {//####[234]####
        schemaValidator.reset();//####[235]####
        schemaValidator.setErrorHandler(null);//####[236]####
        try {//####[238]####
            schemaValidator.validate(source);//####[240]####
        } catch (SAXException e) {//####[246]####
            Context.getOut().print("\tas " + source.getClass().getName());//####[247]####
            Context.getOut().println(" failed. (Incorrect result)" + Arrays.toString(loops));//####[248]####
            e.printStackTrace(Context.getOut());//####[249]####
        } catch (IOException e) {//####[250]####
            Context.getOut().println("Unable to validate due to IOException.");//####[251]####
        }//####[252]####
    }//####[253]####
}//####[253]####
