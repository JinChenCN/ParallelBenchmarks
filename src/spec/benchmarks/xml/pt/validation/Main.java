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
    private static int THREADSNUM = 4;//####[39]####
//####[40]####
    private static int CHUNCK_NUM = 4;//####[40]####
//####[41]####
    private static ParaTask.ScheduleType scheduleType = ParaTask.ScheduleType.WorkSharing;//####[41]####
//####[43]####
    public static String testType() {//####[43]####
        return MULTI;//####[44]####
    }//####[45]####
//####[46]####
    private static String[] schemaNames = { "validation_input.xsd", "periodic_table.xsd", "play.xsd", "structure.xsd", "po.xsd", "personal.xsd" };//####[46]####
//####[54]####
    private static String[] instanceNames = { "validation_input.xml", "periodicxsd.xml", "much_adoxsd.xml", "structure.xml", "po.xml", "personal.xml" };//####[54]####
//####[72]####
    private static int loops[] = { 1, 5, 3, 52, 647, 419 };//####[72]####
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
                    Validator[] validatorForLoops = new Validator[CHUNCK_NUM];//####[122]####
                    for (int k = 0; k < CHUNCK_NUM; k++) //####[123]####
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
            System.out.println("PT Parallel xml validation has taken  " + (time / 1000.0) + " seconds.");//####[148]####
        } catch (Exception e) {//####[149]####
            e.printStackTrace(Context.getOut());//####[150]####
        }//####[151]####
    }//####[152]####
//####[154]####
    public static void main(String[] args) throws Exception {//####[154]####
        ParaTask.init();//####[155]####
        runSimple(Main.class, args);//####[156]####
    }//####[157]####
//####[159]####
    private void executeWorkload() throws ParserConfigurationException, IOException, SAXException {//####[160]####
        ParaTask.setScheduling(scheduleType);//####[162]####
        ParaTask.setThreadPoolSize(ParaTask.ThreadPoolType.ONEOFF, THREADSNUM);//####[163]####
        TaskIDGroup g;//####[165]####
        g = new TaskIDGroup(XSD_NUMBER);//####[166]####
        for (int i = 0; i < XSD_NUMBER; i++) //####[168]####
        {//####[168]####
            Context.getOut().println("Validating " + instanceNames[i]);//####[169]####
            TaskID id = doValidationTests(loops[i], allInstanceBytes[i], schemaBoundValidator[i]);//####[172]####
            g.add(id);//####[173]####
        }//####[174]####
        try {//####[175]####
            g.waitTillFinished();//####[176]####
        } catch (Exception e) {//####[177]####
            e.printStackTrace();//####[178]####
        }//####[179]####
    }//####[180]####
//####[182]####
    private static volatile Method __pt__doValidationTests_int_CachedFile_ValidatorAr_method = null;//####[182]####
    private synchronized static void __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet() {//####[182]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[182]####
            try {//####[182]####
                __pt__doValidationTests_int_CachedFile_ValidatorAr_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__doValidationTests", new Class[] {//####[182]####
                    int.class, CachedFile.class, Validator[].class//####[182]####
                });//####[182]####
            } catch (Exception e) {//####[182]####
                e.printStackTrace();//####[182]####
            }//####[182]####
        }//####[182]####
    }//####[182]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[184]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[184]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[184]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setTaskIdArgIndexes(0);//####[184]####
        taskinfo.addDependsOn(loops);//####[184]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[184]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[184]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setQueueArgIndexes(0);//####[184]####
        taskinfo.setIsPipeline(true);//####[184]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[184]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[184]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setTaskIdArgIndexes(1);//####[184]####
        taskinfo.addDependsOn(file);//####[184]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[184]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[184]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[184]####
        taskinfo.addDependsOn(loops);//####[184]####
        taskinfo.addDependsOn(file);//####[184]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[184]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[184]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setQueueArgIndexes(0);//####[184]####
        taskinfo.setIsPipeline(true);//####[184]####
        taskinfo.setTaskIdArgIndexes(1);//####[184]####
        taskinfo.addDependsOn(file);//####[184]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[184]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[184]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setQueueArgIndexes(1);//####[184]####
        taskinfo.setIsPipeline(true);//####[184]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[184]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[184]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setQueueArgIndexes(1);//####[184]####
        taskinfo.setIsPipeline(true);//####[184]####
        taskinfo.setTaskIdArgIndexes(0);//####[184]####
        taskinfo.addDependsOn(loops);//####[184]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[184]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[184]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setQueueArgIndexes(0, 1);//####[184]####
        taskinfo.setIsPipeline(true);//####[184]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[184]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[184]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setTaskIdArgIndexes(2);//####[184]####
        taskinfo.addDependsOn(schemaValidator);//####[184]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[184]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[184]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[184]####
        taskinfo.addDependsOn(loops);//####[184]####
        taskinfo.addDependsOn(schemaValidator);//####[184]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[184]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[184]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setQueueArgIndexes(0);//####[184]####
        taskinfo.setIsPipeline(true);//####[184]####
        taskinfo.setTaskIdArgIndexes(2);//####[184]####
        taskinfo.addDependsOn(schemaValidator);//####[184]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[184]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[184]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[184]####
        taskinfo.addDependsOn(file);//####[184]####
        taskinfo.addDependsOn(schemaValidator);//####[184]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[184]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[184]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setTaskIdArgIndexes(0, 1, 2);//####[184]####
        taskinfo.addDependsOn(loops);//####[184]####
        taskinfo.addDependsOn(file);//####[184]####
        taskinfo.addDependsOn(schemaValidator);//####[184]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[184]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[184]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setQueueArgIndexes(0);//####[184]####
        taskinfo.setIsPipeline(true);//####[184]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[184]####
        taskinfo.addDependsOn(file);//####[184]####
        taskinfo.addDependsOn(schemaValidator);//####[184]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[184]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[184]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setQueueArgIndexes(1);//####[184]####
        taskinfo.setIsPipeline(true);//####[184]####
        taskinfo.setTaskIdArgIndexes(2);//####[184]####
        taskinfo.addDependsOn(schemaValidator);//####[184]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[184]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[184]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setQueueArgIndexes(1);//####[184]####
        taskinfo.setIsPipeline(true);//####[184]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[184]####
        taskinfo.addDependsOn(loops);//####[184]####
        taskinfo.addDependsOn(schemaValidator);//####[184]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[184]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[184]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setQueueArgIndexes(0, 1);//####[184]####
        taskinfo.setIsPipeline(true);//####[184]####
        taskinfo.setTaskIdArgIndexes(2);//####[184]####
        taskinfo.addDependsOn(schemaValidator);//####[184]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[184]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[184]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setQueueArgIndexes(2);//####[184]####
        taskinfo.setIsPipeline(true);//####[184]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[184]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[184]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setQueueArgIndexes(2);//####[184]####
        taskinfo.setIsPipeline(true);//####[184]####
        taskinfo.setTaskIdArgIndexes(0);//####[184]####
        taskinfo.addDependsOn(loops);//####[184]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[184]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[184]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setQueueArgIndexes(0, 2);//####[184]####
        taskinfo.setIsPipeline(true);//####[184]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[184]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[184]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setQueueArgIndexes(2);//####[184]####
        taskinfo.setIsPipeline(true);//####[184]####
        taskinfo.setTaskIdArgIndexes(1);//####[184]####
        taskinfo.addDependsOn(file);//####[184]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[184]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[184]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setQueueArgIndexes(2);//####[184]####
        taskinfo.setIsPipeline(true);//####[184]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[184]####
        taskinfo.addDependsOn(loops);//####[184]####
        taskinfo.addDependsOn(file);//####[184]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[184]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[184]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setQueueArgIndexes(0, 2);//####[184]####
        taskinfo.setIsPipeline(true);//####[184]####
        taskinfo.setTaskIdArgIndexes(1);//####[184]####
        taskinfo.addDependsOn(file);//####[184]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[184]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[184]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setQueueArgIndexes(1, 2);//####[184]####
        taskinfo.setIsPipeline(true);//####[184]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[184]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[184]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setQueueArgIndexes(1, 2);//####[184]####
        taskinfo.setIsPipeline(true);//####[184]####
        taskinfo.setTaskIdArgIndexes(0);//####[184]####
        taskinfo.addDependsOn(loops);//####[184]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[184]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[184]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[184]####
    }//####[184]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        // ensure Method variable is set//####[184]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[184]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[184]####
        }//####[184]####
        taskinfo.setQueueArgIndexes(0, 1, 2);//####[184]####
        taskinfo.setIsPipeline(true);//####[184]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[184]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[184]####
        taskinfo.setInstance(this);//####[184]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[184]####
    }//####[184]####
    public void __pt__doValidationTests(int loops, CachedFile file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[184]####
        if (loops < THREADSNUM) //####[185]####
        {//####[186]####
            for (int i = loops - 1; i >= 0; i--) //####[187]####
            {//####[187]####
                validateSource(i, createDomSource(file), schemaValidator[i]);//####[188]####
                validateSource(i, createSaxSource(file), schemaValidator[i]);//####[189]####
            }//####[190]####
        } else {//####[191]####
            TaskIDGroup g;//####[192]####
            g = new TaskIDGroup(CHUNCK_NUM);//####[193]####
            int[] loopsForThread = new int[CHUNCK_NUM];//####[194]####
            for (int i = 0; i < CHUNCK_NUM - 1; i++) //####[195]####
            {//####[196]####
                loopsForThread[i] = loops / CHUNCK_NUM;//####[197]####
            }//####[198]####
            loopsForThread[CHUNCK_NUM - 1] = loops - (CHUNCK_NUM - 1) * loops / CHUNCK_NUM;//####[199]####
            for (int j = 0; j < CHUNCK_NUM; j++) //####[201]####
            {//####[202]####
                TaskID id = doValidationLoop(loopsForThread[j], file, schemaValidator[j]);//####[203]####
                g.add(id);//####[204]####
            }//####[205]####
            try {//####[212]####
                g.waitTillFinished();//####[213]####
            } catch (Exception e) {//####[214]####
                e.printStackTrace();//####[215]####
            }//####[216]####
        }//####[217]####
    }//####[219]####
//####[219]####
//####[221]####
    private static volatile Method __pt__doValidationLoop_int_CachedFile_Validator_method = null;//####[221]####
    private synchronized static void __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet() {//####[221]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[221]####
            try {//####[221]####
                __pt__doValidationLoop_int_CachedFile_Validator_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__doValidationLoop", new Class[] {//####[221]####
                    int.class, CachedFile.class, Validator.class//####[221]####
                });//####[221]####
            } catch (Exception e) {//####[221]####
                e.printStackTrace();//####[221]####
            }//####[221]####
        }//####[221]####
    }//####[221]####
    private TaskID<Void> doValidationLoop(int loop, CachedFile file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[222]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(int loop, CachedFile file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        // ensure Method variable is set//####[222]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[222]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[222]####
        }//####[222]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[222]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[222]####
        taskinfo.setInstance(this);//####[222]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, CachedFile file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[222]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, CachedFile file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        // ensure Method variable is set//####[222]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[222]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[222]####
        }//####[222]####
        taskinfo.setTaskIdArgIndexes(0);//####[222]####
        taskinfo.addDependsOn(loop);//####[222]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[222]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[222]####
        taskinfo.setInstance(this);//####[222]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, CachedFile file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[222]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, CachedFile file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        // ensure Method variable is set//####[222]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[222]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[222]####
        }//####[222]####
        taskinfo.setQueueArgIndexes(0);//####[222]####
        taskinfo.setIsPipeline(true);//####[222]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[222]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[222]####
        taskinfo.setInstance(this);//####[222]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(int loop, TaskID<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[222]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(int loop, TaskID<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        // ensure Method variable is set//####[222]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[222]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[222]####
        }//####[222]####
        taskinfo.setTaskIdArgIndexes(1);//####[222]####
        taskinfo.addDependsOn(file);//####[222]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[222]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[222]####
        taskinfo.setInstance(this);//####[222]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, TaskID<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[222]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, TaskID<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        // ensure Method variable is set//####[222]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[222]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[222]####
        }//####[222]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[222]####
        taskinfo.addDependsOn(loop);//####[222]####
        taskinfo.addDependsOn(file);//####[222]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[222]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[222]####
        taskinfo.setInstance(this);//####[222]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, TaskID<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[222]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, TaskID<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        // ensure Method variable is set//####[222]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[222]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[222]####
        }//####[222]####
        taskinfo.setQueueArgIndexes(0);//####[222]####
        taskinfo.setIsPipeline(true);//####[222]####
        taskinfo.setTaskIdArgIndexes(1);//####[222]####
        taskinfo.addDependsOn(file);//####[222]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[222]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[222]####
        taskinfo.setInstance(this);//####[222]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(int loop, BlockingQueue<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[222]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(int loop, BlockingQueue<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        // ensure Method variable is set//####[222]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[222]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[222]####
        }//####[222]####
        taskinfo.setQueueArgIndexes(1);//####[222]####
        taskinfo.setIsPipeline(true);//####[222]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[222]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[222]####
        taskinfo.setInstance(this);//####[222]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, BlockingQueue<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[222]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, BlockingQueue<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        // ensure Method variable is set//####[222]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[222]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[222]####
        }//####[222]####
        taskinfo.setQueueArgIndexes(1);//####[222]####
        taskinfo.setIsPipeline(true);//####[222]####
        taskinfo.setTaskIdArgIndexes(0);//####[222]####
        taskinfo.addDependsOn(loop);//####[222]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[222]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[222]####
        taskinfo.setInstance(this);//####[222]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, BlockingQueue<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[222]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, BlockingQueue<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        // ensure Method variable is set//####[222]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[222]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[222]####
        }//####[222]####
        taskinfo.setQueueArgIndexes(0, 1);//####[222]####
        taskinfo.setIsPipeline(true);//####[222]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[222]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[222]####
        taskinfo.setInstance(this);//####[222]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(int loop, CachedFile file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[222]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(int loop, CachedFile file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        // ensure Method variable is set//####[222]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[222]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[222]####
        }//####[222]####
        taskinfo.setTaskIdArgIndexes(2);//####[222]####
        taskinfo.addDependsOn(schemaValidator);//####[222]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[222]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[222]####
        taskinfo.setInstance(this);//####[222]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, CachedFile file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[222]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, CachedFile file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        // ensure Method variable is set//####[222]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[222]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[222]####
        }//####[222]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[222]####
        taskinfo.addDependsOn(loop);//####[222]####
        taskinfo.addDependsOn(schemaValidator);//####[222]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[222]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[222]####
        taskinfo.setInstance(this);//####[222]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, CachedFile file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[222]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, CachedFile file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        // ensure Method variable is set//####[222]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[222]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[222]####
        }//####[222]####
        taskinfo.setQueueArgIndexes(0);//####[222]####
        taskinfo.setIsPipeline(true);//####[222]####
        taskinfo.setTaskIdArgIndexes(2);//####[222]####
        taskinfo.addDependsOn(schemaValidator);//####[222]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[222]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[222]####
        taskinfo.setInstance(this);//####[222]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(int loop, TaskID<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[222]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(int loop, TaskID<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        // ensure Method variable is set//####[222]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[222]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[222]####
        }//####[222]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[222]####
        taskinfo.addDependsOn(file);//####[222]####
        taskinfo.addDependsOn(schemaValidator);//####[222]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[222]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[222]####
        taskinfo.setInstance(this);//####[222]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, TaskID<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[222]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, TaskID<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        // ensure Method variable is set//####[222]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[222]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[222]####
        }//####[222]####
        taskinfo.setTaskIdArgIndexes(0, 1, 2);//####[222]####
        taskinfo.addDependsOn(loop);//####[222]####
        taskinfo.addDependsOn(file);//####[222]####
        taskinfo.addDependsOn(schemaValidator);//####[222]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[222]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[222]####
        taskinfo.setInstance(this);//####[222]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, TaskID<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[222]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, TaskID<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        // ensure Method variable is set//####[222]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[222]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[222]####
        }//####[222]####
        taskinfo.setQueueArgIndexes(0);//####[222]####
        taskinfo.setIsPipeline(true);//####[222]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[222]####
        taskinfo.addDependsOn(file);//####[222]####
        taskinfo.addDependsOn(schemaValidator);//####[222]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[222]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[222]####
        taskinfo.setInstance(this);//####[222]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(int loop, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[222]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(int loop, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        // ensure Method variable is set//####[222]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[222]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[222]####
        }//####[222]####
        taskinfo.setQueueArgIndexes(1);//####[222]####
        taskinfo.setIsPipeline(true);//####[222]####
        taskinfo.setTaskIdArgIndexes(2);//####[222]####
        taskinfo.addDependsOn(schemaValidator);//####[222]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[222]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[222]####
        taskinfo.setInstance(this);//####[222]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[222]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        // ensure Method variable is set//####[222]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[222]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[222]####
        }//####[222]####
        taskinfo.setQueueArgIndexes(1);//####[222]####
        taskinfo.setIsPipeline(true);//####[222]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[222]####
        taskinfo.addDependsOn(loop);//####[222]####
        taskinfo.addDependsOn(schemaValidator);//####[222]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[222]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[222]####
        taskinfo.setInstance(this);//####[222]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[222]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        // ensure Method variable is set//####[222]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[222]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[222]####
        }//####[222]####
        taskinfo.setQueueArgIndexes(0, 1);//####[222]####
        taskinfo.setIsPipeline(true);//####[222]####
        taskinfo.setTaskIdArgIndexes(2);//####[222]####
        taskinfo.addDependsOn(schemaValidator);//####[222]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[222]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[222]####
        taskinfo.setInstance(this);//####[222]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(int loop, CachedFile file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[222]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(int loop, CachedFile file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        // ensure Method variable is set//####[222]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[222]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[222]####
        }//####[222]####
        taskinfo.setQueueArgIndexes(2);//####[222]####
        taskinfo.setIsPipeline(true);//####[222]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[222]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[222]####
        taskinfo.setInstance(this);//####[222]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, CachedFile file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[222]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, CachedFile file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        // ensure Method variable is set//####[222]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[222]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[222]####
        }//####[222]####
        taskinfo.setQueueArgIndexes(2);//####[222]####
        taskinfo.setIsPipeline(true);//####[222]####
        taskinfo.setTaskIdArgIndexes(0);//####[222]####
        taskinfo.addDependsOn(loop);//####[222]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[222]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[222]####
        taskinfo.setInstance(this);//####[222]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, CachedFile file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[222]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, CachedFile file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        // ensure Method variable is set//####[222]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[222]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[222]####
        }//####[222]####
        taskinfo.setQueueArgIndexes(0, 2);//####[222]####
        taskinfo.setIsPipeline(true);//####[222]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[222]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[222]####
        taskinfo.setInstance(this);//####[222]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(int loop, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[222]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(int loop, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        // ensure Method variable is set//####[222]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[222]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[222]####
        }//####[222]####
        taskinfo.setQueueArgIndexes(2);//####[222]####
        taskinfo.setIsPipeline(true);//####[222]####
        taskinfo.setTaskIdArgIndexes(1);//####[222]####
        taskinfo.addDependsOn(file);//####[222]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[222]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[222]####
        taskinfo.setInstance(this);//####[222]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[222]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        // ensure Method variable is set//####[222]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[222]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[222]####
        }//####[222]####
        taskinfo.setQueueArgIndexes(2);//####[222]####
        taskinfo.setIsPipeline(true);//####[222]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[222]####
        taskinfo.addDependsOn(loop);//####[222]####
        taskinfo.addDependsOn(file);//####[222]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[222]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[222]####
        taskinfo.setInstance(this);//####[222]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[222]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        // ensure Method variable is set//####[222]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[222]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[222]####
        }//####[222]####
        taskinfo.setQueueArgIndexes(0, 2);//####[222]####
        taskinfo.setIsPipeline(true);//####[222]####
        taskinfo.setTaskIdArgIndexes(1);//####[222]####
        taskinfo.addDependsOn(file);//####[222]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[222]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[222]####
        taskinfo.setInstance(this);//####[222]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(int loop, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[222]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(int loop, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        // ensure Method variable is set//####[222]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[222]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[222]####
        }//####[222]####
        taskinfo.setQueueArgIndexes(1, 2);//####[222]####
        taskinfo.setIsPipeline(true);//####[222]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[222]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[222]####
        taskinfo.setInstance(this);//####[222]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[222]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        // ensure Method variable is set//####[222]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[222]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[222]####
        }//####[222]####
        taskinfo.setQueueArgIndexes(1, 2);//####[222]####
        taskinfo.setIsPipeline(true);//####[222]####
        taskinfo.setTaskIdArgIndexes(0);//####[222]####
        taskinfo.addDependsOn(loop);//####[222]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[222]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[222]####
        taskinfo.setInstance(this);//####[222]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[222]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[222]####
    }//####[222]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        // ensure Method variable is set//####[222]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[222]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[222]####
        }//####[222]####
        taskinfo.setQueueArgIndexes(0, 1, 2);//####[222]####
        taskinfo.setIsPipeline(true);//####[222]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[222]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[222]####
        taskinfo.setInstance(this);//####[222]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[222]####
    }//####[222]####
    public void __pt__doValidationLoop(int loop, CachedFile file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[222]####
        for (int i = loop - 1; i >= 0; i--) //####[223]####
        {//####[223]####
            validateSource(i, createDomSource(file), schemaValidator);//####[224]####
            validateSource(i, createSaxSource(file), schemaValidator);//####[225]####
        }//####[226]####
    }//####[229]####
//####[229]####
//####[235]####
    private void validateSource(int loop, Source source, Validator schemaValidator) {//####[235]####
        schemaValidator.reset();//####[236]####
        schemaValidator.setErrorHandler(null);//####[237]####
        try {//####[239]####
            schemaValidator.validate(source);//####[241]####
        } catch (SAXException e) {//####[247]####
            Context.getOut().print("\tas " + source.getClass().getName());//####[248]####
            Context.getOut().println(" failed. (Incorrect result)" + Arrays.toString(loops));//####[249]####
            e.printStackTrace(Context.getOut());//####[250]####
        } catch (IOException e) {//####[251]####
            Context.getOut().println("Unable to validate due to IOException.");//####[252]####
        }//####[253]####
    }//####[254]####
}//####[254]####
