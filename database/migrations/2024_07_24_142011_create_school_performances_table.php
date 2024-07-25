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

        Schema::create('school_performances', function (Blueprint $table) {
            $table->id();
            $table->string('school_id');
            $table->year('year');
            $table->integer('score');
            $table->timestamps();

            $table->foreign('school_id')->references('school_id')->on('schools')->onDelete('cascade');
        });

        Schema::enableForeignKeyConstraints();

    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {

        Schema::disableForeignKeyConstraints();

        Schema::dropIfExists('school_performances');

        Schema::enableForeignKeyConstraints();

    }
};
