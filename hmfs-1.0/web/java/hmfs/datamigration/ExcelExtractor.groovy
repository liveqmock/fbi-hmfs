package hmfs.datamigration

import groovy.sql.Sql

/**
 * 数据移植 excel 数据导入.
 * User: zhanrui
 * Date: 12-8-16
 * Time: 上午10:31
 * To change this template use File | Settings | File Templates.
 */
class ExcelExtractor {
    def dbparam = [url: 'jdbc:oracle:thin:@localhost:1521:orcl', user: 'hmfs', password: 'hmfs', driver: 'oracle.jdbc.driver.OracleDriver']
    def db = Sql.newInstance(dbparam.url, dbparam.user, dbparam.password, dbparam.driver)
    def mainpath = "c:/temp/"
    def commitnum = 2000

    static void main(args) {
        ExcelExtractor extractor = new ExcelExtractor()
        extractor.readExcel("mig_acct", "acct.xls")
    }

    def readExcel(table, file) {
        db.execute("truncate table " + table)

        def builder = new ExcelBuilder(mainpath + "${file}")
        def sheetnum = builder.workbook._sheets.size - 1
        def colnum = builder.workbook._sheets[0]._rows[0].row.field_3_last_col
        def param = ""
        (0..colnum - 1).each {param = param + '?' + ','}
        param = param[0..-2]

        db.withBatch(commitnum, """insert into $table
                 values ($param) """) {ps ->
            (0..sheetnum).each {idx ->
                builder.eachLine([labels: true, sheet: idx]) {
                    (0..colnum - 1).each {i -> ps.setObject(i + 1, cell(i)) }
                    ps.addBatch()
                }
            }
        }
    }
}
