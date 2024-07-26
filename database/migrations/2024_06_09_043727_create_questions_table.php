<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::disableForeignKeyConstraints();

        Schema::create('questions', function (Blueprint $table) {
            $table->id('question_id')->primary();
            $table->string('text');
            $table->integer('marks');
            $table->integer('total_times_answered_correctly')->default(0);
            $table->unsignedBigInteger('challenge_id');
            $table->timestamps();

            $table->foreign('challenge_id')->references('challenge_id')->on('challenges')->onDelete('cascade');
        });

        Schema::enableForeignKeyConstraints();
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::disableForeignKeyConstraints();

        Schema::dropIfExists('questions');

        Schema::enableForeignKeyConstraints();
    }
};
