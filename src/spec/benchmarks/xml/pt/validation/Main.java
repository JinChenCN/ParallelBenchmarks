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
//####[41]####
    private static int CHUNK_NUM = 4;//####[41]####
//####[44]####
    public static String testType() {//####[44]####
        return MULTI;//####[45]####
    }//####[46]####
//####[48]####
    private static String[] schemaNames = { "validation_input.xsd", "periodic_table.xsd", "play.xsd", "structure.xsd", "po.xsd", "personal.xsd" };//####[48]####
//####[57]####
    private static String[] instanceNames = { "validation_input.xml", "periodicxsd.xml", "much_adoxsd.xml", "structure.xml", "po.xml", "personal.xml" };//####[57]####
//####[75]####
    private static int loops[] = { 1, 5, 3, 52, 647, 419 };//####[75]####
//####[85]####
    public static void setupBenchmark() {//####[85]####
        String dirName = Util.getProperty(Constants.XML_VALIDATION_INPUT_DIR_PROP, null);//####[86]####
        try {//####[87]####
            allInstanceBytes = new FileCache.CachedFile[XSD_NUMBER];//####[88]####
            FileCache cache = Context.getFileCache();//####[89]####
            for (int i = 0; i < XSD_NUMBER; i++) //####[90]####
            {//####[90]####
                String name = getFullName(Main.class, dirName, instanceNames[i]);//####[91]####
                allInstanceBytes[i] = cache.new CachedFile(name);//####[92]####
                allInstanceBytes[i].cache();//####[93]####
            }//####[94]####
            allSchemaBytes = new FileCache.CachedFile[XSD_NUMBER];//####[95]####
            for (int i = 0; i < XSD_NUMBER; i++) //####[96]####
            {//####[96]####
                String name = getFullName(Main.class, dirName, schemaNames[i]);//####[97]####
                allSchemaBytes[i] = cache.new CachedFile(name);//####[98]####
                allSchemaBytes[i].cache();//####[99]####
            }//####[100]####
            setupValidators(dirName);//####[102]####
        } catch (IOException e) {//####[103]####
            e.printStackTrace(Context.getOut());//####[104]####
        }//####[105]####
    }//####[106]####
//####[108]####
    private static void setupValidators(String dirName) {//####[108]####
        int threads = Launch.currentNumberBmThreads;//####[109]####
        allValidators = new Validator[threads][XSD_NUMBER][];//####[110]####
        try {//####[111]####
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);//####[112]####
            sf.setErrorHandler(null);//####[113]####
            for (int i = 0; i < XSD_NUMBER; i++) //####[114]####
            {//####[114]####
                String xsdFilename = getFullName(Main.class, dirName, schemaNames[i]);//####[115]####
                File tempURI = new File(xsdFilename);//####[116]####
                Schema precompSchema;//####[117]####
                if (tempURI.isAbsolute()) //####[118]####
                {//####[118]####
                    precompSchema = sf.newSchema(new StreamSource(allSchemaBytes[i].getStream(), tempURI.toURI().toString()));//####[119]####
                } else {//####[121]####
                    precompSchema = sf.newSchema(new StreamSource(allSchemaBytes[i].getStream(), xsdFilename));//####[122]####
                }//####[123]####
                for (int j = 0; j < threads; j++) //####[124]####
                {//####[124]####
                    Validator[] validatorForLoops = new Validator[CHUNK_NUM];//####[125]####
                    for (int k = 0; k < CHUNK_NUM; k++) //####[126]####
                    {//####[126]####
                        validatorForLoops[k] = precompSchema.newValidator();//####[127]####
                    }//####[128]####
                    allValidators[j][i] = validatorForLoops;//####[129]####
                }//####[130]####
            }//####[131]####
        } catch (SAXException e) {//####[132]####
            e.printStackTrace();//####[133]####
        } catch (Exception e) {//####[134]####
            e.printStackTrace();//####[135]####
        }//####[136]####
    }//####[137]####
//####[139]####
    private Validator[][] schemaBoundValidator;//####[139]####
//####[141]####
    public Main(BenchmarkResult bmResult, int threadId) {//####[141]####
        super(bmResult, threadId);//####[142]####
        schemaBoundValidator = allValidators[threadId - 1];//####[143]####
    }//####[144]####
//####[146]####
    public void harnessMain() {//####[146]####
        try {//####[147]####
            long start = System.currentTimeMillis();//####[148]####
            executeWorkload();//####[149]####
            long time = System.currentTimeMillis() - start;//####[150]####
            System.out.println("PT Parallel xml validation has taken  " + (time / 1000.0) + " seconds.");//####[151]####
        } catch (Exception e) {//####[152]####
            e.printStackTrace(Context.getOut());//####[153]####
        }//####[154]####
    }//####[155]####
//####[157]####
    public static void main(String[] args) throws Exception {//####[157]####
        ParaTask.init();//####[158]####
        runSimple(Main.class, args);//####[159]####
    }//####[160]####
//####[162]####
    private void executeWorkload() throws ParserConfigurationException, IOException, SAXException {//####[163]####
        ParaTask.setThreadPoolSize(ParaTask.ThreadPoolType.ONEOFF, THREADSNUM);//####[166]####
        TaskIDGroup g;//####[168]####
        g = new TaskIDGroup(XSD_NUMBER);//####[169]####
        for (int i = 0; i < XSD_NUMBER; i++) //####[171]####
        {//####[171]####
            Context.getOut().println("Validating " + instanceNames[i]);//####[172]####
            TaskID id = doValidationTests(loops[i], allInstanceBytes[i], schemaBoundValidator[i]);//####[175]####
            g.add(id);//####[176]####
        }//####[177]####
        try {//####[178]####
            g.waitTillFinished();//####[179]####
        } catch (Exception e) {//####[180]####
            e.printStackTrace();//####[181]####
        }//####[182]####
    }//####[183]####
//####[185]####
    private static volatile Method __pt__doValidationTests_int_CachedFile_ValidatorAr_method = null;//####[185]####
    private synchronized static void __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet() {//####[185]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[185]####
            try {//####[185]####
                __pt__doValidationTests_int_CachedFile_ValidatorAr_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__doValidationTests", new Class[] {//####[185]####
                    int.class, CachedFile.class, Validator[].class//####[185]####
                });//####[185]####
            } catch (Exception e) {//####[185]####
                e.printStackTrace();//####[185]####
            }//####[185]####
        }//####[185]####
    }//####[185]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[187]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[187]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[187]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setTaskIdArgIndexes(0);//####[187]####
        taskinfo.addDependsOn(loops);//####[187]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[187]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[187]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setQueueArgIndexes(0);//####[187]####
        taskinfo.setIsPipeline(true);//####[187]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[187]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[187]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setTaskIdArgIndexes(1);//####[187]####
        taskinfo.addDependsOn(file);//####[187]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[187]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[187]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[187]####
        taskinfo.addDependsOn(loops);//####[187]####
        taskinfo.addDependsOn(file);//####[187]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[187]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[187]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setQueueArgIndexes(0);//####[187]####
        taskinfo.setIsPipeline(true);//####[187]####
        taskinfo.setTaskIdArgIndexes(1);//####[187]####
        taskinfo.addDependsOn(file);//####[187]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[187]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[187]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setQueueArgIndexes(1);//####[187]####
        taskinfo.setIsPipeline(true);//####[187]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[187]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[187]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setQueueArgIndexes(1);//####[187]####
        taskinfo.setIsPipeline(true);//####[187]####
        taskinfo.setTaskIdArgIndexes(0);//####[187]####
        taskinfo.addDependsOn(loops);//####[187]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[187]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, Validator[] schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[187]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setQueueArgIndexes(0, 1);//####[187]####
        taskinfo.setIsPipeline(true);//####[187]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[187]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[187]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setTaskIdArgIndexes(2);//####[187]####
        taskinfo.addDependsOn(schemaValidator);//####[187]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[187]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[187]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[187]####
        taskinfo.addDependsOn(loops);//####[187]####
        taskinfo.addDependsOn(schemaValidator);//####[187]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[187]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[187]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setQueueArgIndexes(0);//####[187]####
        taskinfo.setIsPipeline(true);//####[187]####
        taskinfo.setTaskIdArgIndexes(2);//####[187]####
        taskinfo.addDependsOn(schemaValidator);//####[187]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[187]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[187]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[187]####
        taskinfo.addDependsOn(file);//####[187]####
        taskinfo.addDependsOn(schemaValidator);//####[187]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[187]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[187]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setTaskIdArgIndexes(0, 1, 2);//####[187]####
        taskinfo.addDependsOn(loops);//####[187]####
        taskinfo.addDependsOn(file);//####[187]####
        taskinfo.addDependsOn(schemaValidator);//####[187]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[187]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[187]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setQueueArgIndexes(0);//####[187]####
        taskinfo.setIsPipeline(true);//####[187]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[187]####
        taskinfo.addDependsOn(file);//####[187]####
        taskinfo.addDependsOn(schemaValidator);//####[187]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[187]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[187]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setQueueArgIndexes(1);//####[187]####
        taskinfo.setIsPipeline(true);//####[187]####
        taskinfo.setTaskIdArgIndexes(2);//####[187]####
        taskinfo.addDependsOn(schemaValidator);//####[187]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[187]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[187]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setQueueArgIndexes(1);//####[187]####
        taskinfo.setIsPipeline(true);//####[187]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[187]####
        taskinfo.addDependsOn(loops);//####[187]####
        taskinfo.addDependsOn(schemaValidator);//####[187]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[187]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, TaskID<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, TaskID<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[187]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setQueueArgIndexes(0, 1);//####[187]####
        taskinfo.setIsPipeline(true);//####[187]####
        taskinfo.setTaskIdArgIndexes(2);//####[187]####
        taskinfo.addDependsOn(schemaValidator);//####[187]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[187]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(int loops, CachedFile file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[187]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setQueueArgIndexes(2);//####[187]####
        taskinfo.setIsPipeline(true);//####[187]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[187]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, CachedFile file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[187]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setQueueArgIndexes(2);//####[187]####
        taskinfo.setIsPipeline(true);//####[187]####
        taskinfo.setTaskIdArgIndexes(0);//####[187]####
        taskinfo.addDependsOn(loops);//####[187]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[187]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, CachedFile file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[187]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setQueueArgIndexes(0, 2);//####[187]####
        taskinfo.setIsPipeline(true);//####[187]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[187]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(int loops, TaskID<CachedFile> file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[187]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setQueueArgIndexes(2);//####[187]####
        taskinfo.setIsPipeline(true);//####[187]####
        taskinfo.setTaskIdArgIndexes(1);//####[187]####
        taskinfo.addDependsOn(file);//####[187]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[187]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, TaskID<CachedFile> file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[187]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setQueueArgIndexes(2);//####[187]####
        taskinfo.setIsPipeline(true);//####[187]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[187]####
        taskinfo.addDependsOn(loops);//####[187]####
        taskinfo.addDependsOn(file);//####[187]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[187]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, TaskID<CachedFile> file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[187]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setQueueArgIndexes(0, 2);//####[187]####
        taskinfo.setIsPipeline(true);//####[187]####
        taskinfo.setTaskIdArgIndexes(1);//####[187]####
        taskinfo.addDependsOn(file);//####[187]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[187]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(int loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[187]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setQueueArgIndexes(1, 2);//####[187]####
        taskinfo.setIsPipeline(true);//####[187]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[187]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(TaskID<Integer> loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[187]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setQueueArgIndexes(1, 2);//####[187]####
        taskinfo.setIsPipeline(true);//####[187]####
        taskinfo.setTaskIdArgIndexes(0);//####[187]####
        taskinfo.addDependsOn(loops);//####[187]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[187]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator[]> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[187]####
        return doValidationTests(loops, file, schemaValidator, new TaskInfo());//####[187]####
    }//####[187]####
    private TaskID<Void> doValidationTests(BlockingQueue<Integer> loops, BlockingQueue<CachedFile> file, BlockingQueue<Validator[]> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        // ensure Method variable is set//####[187]####
        if (__pt__doValidationTests_int_CachedFile_ValidatorAr_method == null) {//####[187]####
            __pt__doValidationTests_int_CachedFile_ValidatorAr_ensureMethodVarSet();//####[187]####
        }//####[187]####
        taskinfo.setQueueArgIndexes(0, 1, 2);//####[187]####
        taskinfo.setIsPipeline(true);//####[187]####
        taskinfo.setParameters(loops, file, schemaValidator);//####[187]####
        taskinfo.setMethod(__pt__doValidationTests_int_CachedFile_ValidatorAr_method);//####[187]####
        taskinfo.setInstance(this);//####[187]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[187]####
    }//####[187]####
    public void __pt__doValidationTests(int loops, CachedFile file, Validator[] schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[187]####
        if (loops < THREADSNUM) //####[188]####
        {//####[189]####
            for (int i = loops - 1; i >= 0; i--) //####[190]####
            {//####[190]####
                validateSource(i, createDomSource(file), schemaValidator[i]);//####[191]####
                validateSource(i, createSaxSource(file), schemaValidator[i]);//####[192]####
            }//####[193]####
        } else {//####[194]####
            TaskIDGroup g;//####[195]####
            g = new TaskIDGroup(CHUNK_NUM);//####[196]####
            int[] loopsForThread = new int[CHUNK_NUM];//####[197]####
            for (int i = 0; i < CHUNK_NUM - 1; i++) //####[198]####
            {//####[199]####
                loopsForThread[i] = loops / CHUNK_NUM;//####[200]####
            }//####[201]####
            loopsForThread[CHUNK_NUM - 1] = loops - (CHUNK_NUM - 1) * loops / CHUNK_NUM;//####[202]####
            for (int j = 0; j < CHUNK_NUM; j++) //####[204]####
            {//####[205]####
                TaskID id = doValidationLoop(loopsForThread[j], file, schemaValidator[j]);//####[206]####
                g.add(id);//####[207]####
            }//####[208]####
            try {//####[215]####
                g.waitTillFinished();//####[216]####
            } catch (Exception e) {//####[217]####
                e.printStackTrace();//####[218]####
            }//####[219]####
        }//####[220]####
    }//####[222]####
//####[222]####
//####[224]####
    private static volatile Method __pt__doValidationLoop_int_CachedFile_Validator_method = null;//####[224]####
    private synchronized static void __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet() {//####[224]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[224]####
            try {//####[224]####
                __pt__doValidationLoop_int_CachedFile_Validator_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__doValidationLoop", new Class[] {//####[224]####
                    int.class, CachedFile.class, Validator.class//####[224]####
                });//####[224]####
            } catch (Exception e) {//####[224]####
                e.printStackTrace();//####[224]####
            }//####[224]####
        }//####[224]####
    }//####[224]####
    private TaskID<Void> doValidationLoop(int loop, CachedFile file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[225]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(int loop, CachedFile file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        // ensure Method variable is set//####[225]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[225]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[225]####
        }//####[225]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[225]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[225]####
        taskinfo.setInstance(this);//####[225]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, CachedFile file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[225]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, CachedFile file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        // ensure Method variable is set//####[225]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[225]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[225]####
        }//####[225]####
        taskinfo.setTaskIdArgIndexes(0);//####[225]####
        taskinfo.addDependsOn(loop);//####[225]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[225]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[225]####
        taskinfo.setInstance(this);//####[225]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, CachedFile file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[225]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, CachedFile file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        // ensure Method variable is set//####[225]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[225]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[225]####
        }//####[225]####
        taskinfo.setQueueArgIndexes(0);//####[225]####
        taskinfo.setIsPipeline(true);//####[225]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[225]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[225]####
        taskinfo.setInstance(this);//####[225]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(int loop, TaskID<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[225]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(int loop, TaskID<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        // ensure Method variable is set//####[225]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[225]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[225]####
        }//####[225]####
        taskinfo.setTaskIdArgIndexes(1);//####[225]####
        taskinfo.addDependsOn(file);//####[225]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[225]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[225]####
        taskinfo.setInstance(this);//####[225]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, TaskID<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[225]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, TaskID<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        // ensure Method variable is set//####[225]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[225]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[225]####
        }//####[225]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[225]####
        taskinfo.addDependsOn(loop);//####[225]####
        taskinfo.addDependsOn(file);//####[225]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[225]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[225]####
        taskinfo.setInstance(this);//####[225]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, TaskID<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[225]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, TaskID<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        // ensure Method variable is set//####[225]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[225]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[225]####
        }//####[225]####
        taskinfo.setQueueArgIndexes(0);//####[225]####
        taskinfo.setIsPipeline(true);//####[225]####
        taskinfo.setTaskIdArgIndexes(1);//####[225]####
        taskinfo.addDependsOn(file);//####[225]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[225]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[225]####
        taskinfo.setInstance(this);//####[225]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(int loop, BlockingQueue<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[225]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(int loop, BlockingQueue<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        // ensure Method variable is set//####[225]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[225]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[225]####
        }//####[225]####
        taskinfo.setQueueArgIndexes(1);//####[225]####
        taskinfo.setIsPipeline(true);//####[225]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[225]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[225]####
        taskinfo.setInstance(this);//####[225]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, BlockingQueue<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[225]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, BlockingQueue<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        // ensure Method variable is set//####[225]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[225]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[225]####
        }//####[225]####
        taskinfo.setQueueArgIndexes(1);//####[225]####
        taskinfo.setIsPipeline(true);//####[225]####
        taskinfo.setTaskIdArgIndexes(0);//####[225]####
        taskinfo.addDependsOn(loop);//####[225]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[225]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[225]####
        taskinfo.setInstance(this);//####[225]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, BlockingQueue<CachedFile> file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[225]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, BlockingQueue<CachedFile> file, Validator schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        // ensure Method variable is set//####[225]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[225]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[225]####
        }//####[225]####
        taskinfo.setQueueArgIndexes(0, 1);//####[225]####
        taskinfo.setIsPipeline(true);//####[225]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[225]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[225]####
        taskinfo.setInstance(this);//####[225]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(int loop, CachedFile file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[225]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(int loop, CachedFile file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        // ensure Method variable is set//####[225]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[225]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[225]####
        }//####[225]####
        taskinfo.setTaskIdArgIndexes(2);//####[225]####
        taskinfo.addDependsOn(schemaValidator);//####[225]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[225]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[225]####
        taskinfo.setInstance(this);//####[225]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, CachedFile file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[225]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, CachedFile file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        // ensure Method variable is set//####[225]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[225]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[225]####
        }//####[225]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[225]####
        taskinfo.addDependsOn(loop);//####[225]####
        taskinfo.addDependsOn(schemaValidator);//####[225]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[225]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[225]####
        taskinfo.setInstance(this);//####[225]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, CachedFile file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[225]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, CachedFile file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        // ensure Method variable is set//####[225]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[225]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[225]####
        }//####[225]####
        taskinfo.setQueueArgIndexes(0);//####[225]####
        taskinfo.setIsPipeline(true);//####[225]####
        taskinfo.setTaskIdArgIndexes(2);//####[225]####
        taskinfo.addDependsOn(schemaValidator);//####[225]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[225]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[225]####
        taskinfo.setInstance(this);//####[225]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(int loop, TaskID<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[225]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(int loop, TaskID<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        // ensure Method variable is set//####[225]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[225]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[225]####
        }//####[225]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[225]####
        taskinfo.addDependsOn(file);//####[225]####
        taskinfo.addDependsOn(schemaValidator);//####[225]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[225]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[225]####
        taskinfo.setInstance(this);//####[225]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, TaskID<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[225]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, TaskID<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        // ensure Method variable is set//####[225]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[225]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[225]####
        }//####[225]####
        taskinfo.setTaskIdArgIndexes(0, 1, 2);//####[225]####
        taskinfo.addDependsOn(loop);//####[225]####
        taskinfo.addDependsOn(file);//####[225]####
        taskinfo.addDependsOn(schemaValidator);//####[225]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[225]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[225]####
        taskinfo.setInstance(this);//####[225]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, TaskID<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[225]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, TaskID<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        // ensure Method variable is set//####[225]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[225]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[225]####
        }//####[225]####
        taskinfo.setQueueArgIndexes(0);//####[225]####
        taskinfo.setIsPipeline(true);//####[225]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[225]####
        taskinfo.addDependsOn(file);//####[225]####
        taskinfo.addDependsOn(schemaValidator);//####[225]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[225]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[225]####
        taskinfo.setInstance(this);//####[225]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(int loop, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[225]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(int loop, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        // ensure Method variable is set//####[225]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[225]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[225]####
        }//####[225]####
        taskinfo.setQueueArgIndexes(1);//####[225]####
        taskinfo.setIsPipeline(true);//####[225]####
        taskinfo.setTaskIdArgIndexes(2);//####[225]####
        taskinfo.addDependsOn(schemaValidator);//####[225]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[225]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[225]####
        taskinfo.setInstance(this);//####[225]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[225]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        // ensure Method variable is set//####[225]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[225]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[225]####
        }//####[225]####
        taskinfo.setQueueArgIndexes(1);//####[225]####
        taskinfo.setIsPipeline(true);//####[225]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[225]####
        taskinfo.addDependsOn(loop);//####[225]####
        taskinfo.addDependsOn(schemaValidator);//####[225]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[225]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[225]####
        taskinfo.setInstance(this);//####[225]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[225]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, BlockingQueue<CachedFile> file, TaskID<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        // ensure Method variable is set//####[225]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[225]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[225]####
        }//####[225]####
        taskinfo.setQueueArgIndexes(0, 1);//####[225]####
        taskinfo.setIsPipeline(true);//####[225]####
        taskinfo.setTaskIdArgIndexes(2);//####[225]####
        taskinfo.addDependsOn(schemaValidator);//####[225]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[225]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[225]####
        taskinfo.setInstance(this);//####[225]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(int loop, CachedFile file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[225]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(int loop, CachedFile file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        // ensure Method variable is set//####[225]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[225]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[225]####
        }//####[225]####
        taskinfo.setQueueArgIndexes(2);//####[225]####
        taskinfo.setIsPipeline(true);//####[225]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[225]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[225]####
        taskinfo.setInstance(this);//####[225]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, CachedFile file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[225]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, CachedFile file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        // ensure Method variable is set//####[225]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[225]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[225]####
        }//####[225]####
        taskinfo.setQueueArgIndexes(2);//####[225]####
        taskinfo.setIsPipeline(true);//####[225]####
        taskinfo.setTaskIdArgIndexes(0);//####[225]####
        taskinfo.addDependsOn(loop);//####[225]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[225]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[225]####
        taskinfo.setInstance(this);//####[225]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, CachedFile file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[225]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, CachedFile file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        // ensure Method variable is set//####[225]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[225]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[225]####
        }//####[225]####
        taskinfo.setQueueArgIndexes(0, 2);//####[225]####
        taskinfo.setIsPipeline(true);//####[225]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[225]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[225]####
        taskinfo.setInstance(this);//####[225]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(int loop, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[225]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(int loop, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        // ensure Method variable is set//####[225]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[225]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[225]####
        }//####[225]####
        taskinfo.setQueueArgIndexes(2);//####[225]####
        taskinfo.setIsPipeline(true);//####[225]####
        taskinfo.setTaskIdArgIndexes(1);//####[225]####
        taskinfo.addDependsOn(file);//####[225]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[225]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[225]####
        taskinfo.setInstance(this);//####[225]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[225]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        // ensure Method variable is set//####[225]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[225]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[225]####
        }//####[225]####
        taskinfo.setQueueArgIndexes(2);//####[225]####
        taskinfo.setIsPipeline(true);//####[225]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[225]####
        taskinfo.addDependsOn(loop);//####[225]####
        taskinfo.addDependsOn(file);//####[225]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[225]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[225]####
        taskinfo.setInstance(this);//####[225]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[225]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, TaskID<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        // ensure Method variable is set//####[225]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[225]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[225]####
        }//####[225]####
        taskinfo.setQueueArgIndexes(0, 2);//####[225]####
        taskinfo.setIsPipeline(true);//####[225]####
        taskinfo.setTaskIdArgIndexes(1);//####[225]####
        taskinfo.addDependsOn(file);//####[225]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[225]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[225]####
        taskinfo.setInstance(this);//####[225]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(int loop, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[225]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(int loop, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        // ensure Method variable is set//####[225]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[225]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[225]####
        }//####[225]####
        taskinfo.setQueueArgIndexes(1, 2);//####[225]####
        taskinfo.setIsPipeline(true);//####[225]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[225]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[225]####
        taskinfo.setInstance(this);//####[225]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[225]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(TaskID<Integer> loop, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        // ensure Method variable is set//####[225]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[225]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[225]####
        }//####[225]####
        taskinfo.setQueueArgIndexes(1, 2);//####[225]####
        taskinfo.setIsPipeline(true);//####[225]####
        taskinfo.setTaskIdArgIndexes(0);//####[225]####
        taskinfo.addDependsOn(loop);//####[225]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[225]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[225]####
        taskinfo.setInstance(this);//####[225]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[225]####
        return doValidationLoop(loop, file, schemaValidator, new TaskInfo());//####[225]####
    }//####[225]####
    private TaskID<Void> doValidationLoop(BlockingQueue<Integer> loop, BlockingQueue<CachedFile> file, BlockingQueue<Validator> schemaValidator, TaskInfo taskinfo) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        // ensure Method variable is set//####[225]####
        if (__pt__doValidationLoop_int_CachedFile_Validator_method == null) {//####[225]####
            __pt__doValidationLoop_int_CachedFile_Validator_ensureMethodVarSet();//####[225]####
        }//####[225]####
        taskinfo.setQueueArgIndexes(0, 1, 2);//####[225]####
        taskinfo.setIsPipeline(true);//####[225]####
        taskinfo.setParameters(loop, file, schemaValidator);//####[225]####
        taskinfo.setMethod(__pt__doValidationLoop_int_CachedFile_Validator_method);//####[225]####
        taskinfo.setInstance(this);//####[225]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[225]####
    }//####[225]####
    public void __pt__doValidationLoop(int loop, CachedFile file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {//####[225]####
        for (int i = loop - 1; i >= 0; i--) //####[226]####
        {//####[226]####
            validateSource(i, createDomSource(file), schemaValidator);//####[227]####
            validateSource(i, createSaxSource(file), schemaValidator);//####[228]####
        }//####[229]####
    }//####[232]####
//####[232]####
//####[238]####
    private void validateSource(int loop, Source source, Validator schemaValidator) {//####[238]####
        schemaValidator.reset();//####[239]####
        schemaValidator.setErrorHandler(null);//####[240]####
        try {//####[242]####
            schemaValidator.validate(source);//####[244]####
        } catch (SAXException e) {//####[250]####
            Context.getOut().print("\tas " + source.getClass().getName());//####[251]####
            Context.getOut().println(" failed. (Incorrect result)" + Arrays.toString(loops));//####[252]####
            e.printStackTrace(Context.getOut());//####[253]####
        } catch (IOException e) {//####[254]####
            Context.getOut().println("Unable to validate due to IOException.");//####[255]####
        }//####[256]####
    }//####[257]####
}//####[257]####
