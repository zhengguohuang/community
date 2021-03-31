package tech.turl.community.wkhtmltopdf;

import java.io.File;

public class HtmlToPdf {
    // wkhtmltopdf path in the system
    private static final String toPdfTool = "e:/wkhtmltopdf/bin/wkhtmltoimage.exe";

    /**
     * html to pdf
     *
     * @param srcPath html path, which can be a path on the hard disk or a network path
     * @param destPath pdf save path
     * @return returns true if the conversion is successful
     */
    public static boolean convert(String srcPath, String destPath) {
        File file = new File(destPath);
        File parent = file.getParentFile();
        // If the pdf save path does not exist, create the path
        if (!parent.exists()) {
            parent.mkdirs();
        }
        StringBuilder cmd = new StringBuilder();
        if (System.getProperty("os.name").indexOf("Windows") == -1) {
            // Non-windows system
            // toPdfTool = FileUtil.convertSystemFilePath("/home/ubuntu/wkhtmltox/bin/wkhtmltopdf");
        }
        cmd.append(toPdfTool);
        cmd.append(" ");
        /*
         cmd.append(" --header-line");//The line below the header
         //cmd.append(" --header-center Here is the header, here is the header, here is the header, here is the header ");//The middle content of the header
         cmd.append(" --margin-top 3cm ");//Set the top margin of the page (default 10mm)
         cmd.append(" --header-html file:///"+WebUtil.getServletContext().getRealPath("")+FileUtil.convertSystemFilePath("\\style\\pdf\\head.html"));/ / (Add an HTML header, followed by the URL)
         cmd.append(" --header-spacing 5 ");// (Set the distance between header and content, default 0)
         //cmd.append(" --footer-center (footer content set in the center position)");//footer content set in the center position
         cmd.append(" --footer-html file:///"+WebUtil.getServletContext().getRealPath("")+FileUtil.convertSystemFilePath("\\style\\pdf\\foter.html"));/ / (Add an HTML footer followed by the URL)
         cmd.append(" --footer-line");//* display a line on the footer content)
         cmd.append(" --footer-spacing 5 ");// (Set the distance between the footer and the content)
        */
        cmd.append(srcPath);
        cmd.append(" ");
        cmd.append(destPath);

        boolean result = true;
        try {
            Process proc = Runtime.getRuntime().exec(cmd.toString());
            HtmlToPdfInterceptor error = new HtmlToPdfInterceptor(proc.getErrorStream());
            HtmlToPdfInterceptor output = new HtmlToPdfInterceptor(proc.getInputStream());
            error.start();
            output.start();
            proc.waitFor();
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }

        return result;
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        HtmlToPdf.convert("http://www.baidu.com", "d:\\test1.png");
        System.out.println(System.currentTimeMillis() - start);
    }
}
