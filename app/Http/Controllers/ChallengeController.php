<?php

namespace App\Http\Controllers;

use App\Models\Challenge;
use Illuminate\Http\Request;

class ChallengeController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        $challenges = Challenge::all();
        return view('admin.challenges', compact('challenges'));
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        //
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        // $request->validate([
        //     'title' => ['required', 'string', 'max:255'],
        //     'start_date' => ['required', 'date', 'date_format:d/m/Y'],
        //     'end_date' => ['required', 'date', 'date_format:d/m/Y', 'after:start_date'],
        //     'duration' => ['required', 'numeric', 'min:1'],
        //     'number_of_questions' => ['required', 'numeric', 'min:1']
        // ]);

        $challenge = new Challenge();
        $challenge->title = strip_tags($request->input('title'));
        $challenge->start_date = \Carbon\Carbon::createFromFormat('d/m/Y', strip_tags($request->input('start_date')));
        $challenge->end_date = \Carbon\Carbon::createFromFormat('d/m/Y', strip_tags($request->input('end_date')));
        $challenge->duration = strip_tags($request->input('duration'));
        $challenge->number_of_questions = strip_tags($request->input('number_of_questions'));
        $challenge->save();

        return redirect()->route('admin.challenges')->with('success', 'Challenge added successfully');
    }


    /**
     * Display the specified resource.
     */
    public function show(Challenge $challenge)
    {
        //
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(Challenge $challenge)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, Challenge $challenge)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(int $id)
    {
        $record = Challenge::findOrFail($id);
        $deleted_challenge_name = $record->title;
        $record->delete();
        return redirect()->route('admin.challenges')->with("success", "$deleted_challenge_name deleted successfully.");
    }
}
