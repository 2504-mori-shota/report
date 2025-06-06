package com.example.forum.controller;

import com.example.forum.controller.form.CommentForm;
import com.example.forum.controller.form.ReportForm;
import com.example.forum.service.ReportService;
import com.example.forum.service.CommentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class ForumController {
    @Autowired
    ReportService reportService;
    @Autowired
    CommentService commentService;

    /*
     * 投稿内容表示処理
     */
    @GetMapping
    public ModelAndView top(@RequestParam(name = "start_date", required = false)String startDate , @RequestParam(name="end_date", required = false )String endDate) throws ParseException {
        ModelAndView mav = new ModelAndView();
        // 投稿を全件取得
        List<ReportForm> contentData = reportService.findByCreatedDateBetween(startDate , endDate);
        // コメントを全件取得
        List<CommentForm> commentData = commentService.findAllComment();
        // form用の空のentityを準備
        CommentForm commentForm = new CommentForm();
        // 画面遷移先を指定
        mav.setViewName("/top");
        // 投稿データオブジェクトを保管
        mav.addObject("contents", contentData);
        // 準備した空のFormを保管
        mav.addObject("formModel", commentForm);
        //
        mav.addObject("comments", commentData);

        return mav;
    }

    @GetMapping("/new")
    public ModelAndView newContent() {
        ModelAndView mav = new ModelAndView();
        // form用の空のentityを準備
        ReportForm reportForm = new ReportForm();
        // 画面遷移先を指定
        mav.setViewName("/new");
        // 準備した空のFormを保管
        mav.addObject("formModel", reportForm);
       // mav.addObject("errorMessageForm", errorMessages);
        return mav;
    }

    /*
     * 新規投稿処理
     */
    @PostMapping("/add")
    public ModelAndView addContent(
            @Valid @ModelAttribute("formModel") ReportForm reportForm,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model){
        if (result.hasErrors()) {
            //フラッシュメッセージをセット
            redirectAttributes.addFlashAttribute("errorMessageForm", "投稿を入力してください");
            return new ModelAndView("redirect:/new");
        }
        // 投稿をテーブルに格納
        reportService.saveReport(reportForm);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * idに紐づいた投稿を削除
     */
    @DeleteMapping("/delete/{id}")
    public ModelAndView deleteContent(@PathVariable Integer id) {
        // 投稿をテーブルに格納
        reportService.deleteReport(id);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * 編集画面表示処理
     */
    @GetMapping("/edit/{id}")
    public ModelAndView editContent(@PathVariable Integer id) {
        /* ModelAndViewとは？
         * A:HTMLに表示させる情報を入れておく箱
         */
        ModelAndView mav = new ModelAndView();
        // 編集する投稿を取得
        ReportForm report = reportService.editReport(id);
        // 編集する投稿をセット
        mav.addObject("formModel", report);
        // 画面遷移先を指定
        mav.setViewName("/edit");
        return mav;
    }

    // 編集の内容
    @PutMapping("/update/{id}")
    public ModelAndView updateContent(
            @PathVariable Integer id,
            @Valid
            @ModelAttribute("formModel") ReportForm report,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            //フラッシュメッセージをセット
            redirectAttributes.addFlashAttribute("errorMessageForm", "投稿を入力してください");
            redirectAttributes.addFlashAttribute(report);
            return new ModelAndView("redirect:/edit/{id}");
        }
        //UrlParameterのidを更新するentityにセット
        report.setId(id);
        // 編集した投稿を更新
        reportService.saveReport(report);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");

    }

    //idに紐づいて返信
    @PostMapping("/comment")
    public ModelAndView Comment(
            @Valid
            @ModelAttribute("formModel") CommentForm comment,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            //フラッシュメッセージをセット
            redirectAttributes.addFlashAttribute(comment);
            redirectAttributes.addFlashAttribute("reportId",comment.getReportId());
            redirectAttributes.addFlashAttribute("errorMessageComment", "コメントを入力してください");
            return new ModelAndView("redirect:/");
        }
        // 返信の内容をDBに保存
        ReportForm updateReport = reportService.editReport(comment.getReportId());
        reportService.saveReport(updateReport);
        commentService.saveComment(comment);
        return new ModelAndView("redirect:/");
    }

    //idに紐づいて編集(画面遷移)
    @GetMapping("/commentEdit/{id}")
    public ModelAndView editComment(@PathVariable Integer id) {
        /* ModelAndViewとは？
         * A:HTMLに表示させる情報を入れておく箱
         */
        ModelAndView mav = new ModelAndView();
        // 編集する投稿を取得
        CommentForm comment = commentService.editComment(id);
        // 編集する投稿をセット
        mav.addObject("formModel", comment);
        // 画面遷移先を指定
        mav.setViewName("/commentEdit");
        return mav;
    }

    //編集データをtop.htmlに送りリダイレクト
    @PostMapping("/commentUpdate/{id}")
    public ModelAndView updateContent(
            @PathVariable Integer id,
            @Valid
            @ModelAttribute("formModel") CommentForm comment,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            //フラッシュメッセージをセット
            redirectAttributes.addFlashAttribute("errorMessageForm", "投稿を入力してください");
            redirectAttributes.addFlashAttribute(comment);
            return new ModelAndView("redirect:/commentEdit/{id}");
        }
        // UrlParameterのidを更新するentityにセット
        comment.setId(id);
        // 編集した投稿を更新
        commentService.saveComment(comment);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    @DeleteMapping("/commentDelete/{id}")
    public ModelAndView deleteComment(@PathVariable Integer id) {
        // 投稿をテーブルに格納
        commentService.deleteComment(id);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }
}
